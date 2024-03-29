package com.asemlab.coronatracker.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.asemlab.coronatracker.R
import com.asemlab.coronatracker.databinding.ActivityMainBinding
import com.asemlab.coronatracker.models.Country
import com.asemlab.coronatracker.utils.formatNumber
import com.asemlab.coronatracker.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var adapter: CountryAdapter
    lateinit var tempList: MutableList<Country>
    lateinit var binding: ActivityMainBinding
    lateinit var connectManager: ConnectivityManager
    lateinit var alert: AlertDialog.Builder
    var connectInfo: NetworkInfo? = null
    lateinit var global: Country
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        mainViewModel.apply {
            countries.observe(this@MainActivity) {
                addData(it)
            }
            global.observe(this@MainActivity) {
                this@MainActivity.global = it
                this@MainActivity.global.name = "Global"
                binding.globalLayout.apply {
                    globalItem = this@MainActivity.global
                    itemClicked = ::startAlert
                }
            }
        }
        setupAdapter()

        connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnectedOrConnecting) {
            mainViewModel.apply {
                getGlobal()
                getCountries()
            }
        } else {
            binding.loadingBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_internet)
        }

        tempList = mutableListOf()
        binding.searchBar.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(qu: String?): Boolean {
                    adapter.clear()
                    adapter.addAll(tempList.filter {
                        it.name!!.contains(qu?.subSequence(0, qu.length - 1) ?: "", true)
                    } as MutableList<Country>
                    )
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        adapter.clear()
                        adapter.addAll(tempList)

                    }
                    return true
                }
            }
            )

            setOnCloseListener(SearchView.OnCloseListener {
                adapter.clear()
                adapter.addAll(tempList)
                clearFocus()
                return@OnCloseListener true
            }
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            Log.i(MainActivity::class.simpleName, "In refresh swiper")
            refreshList()
        }
        binding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_dark,
            android.R.color.holo_red_dark,
            android.R.color.holo_green_dark
        )
    }

    private fun setupAdapter() {
        adapter = CountryAdapter(this, R.layout.item_layout, mutableListOf())
        binding.listView.adapter = adapter
        binding.listView.setOnItemClickListener { parent, view, position, id ->
            startAlert(adapter.objects[position])
        }
    }

    private fun addData(countries: List<Country>?) {
        binding.loadingBar.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        if (countries == null || countries.isEmpty()) {
            binding.searchBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_data)
            binding.swipeRefresh.isRefreshing = false
            return
        }

        adapter.clear()
        tempList.clear()
        adapter.addAll(countries)
        tempList.addAll(countries)
        adapter.objects.sortBy {
            it.name
        }
        tempList.sortBy {
            it.name
        }
        binding.swipeRefresh.isRefreshing = false
    }

    private fun startAlert(country: Country) {
        alert = AlertDialog.Builder(this)
        val msg: String = "\n${getString(R.string.confirmed, formatNumber(country.newCases))}\n\n" +
                "${getString(R.string.deaths, formatNumber(country.newDeaths))}\n\n" +
                "${getString(R.string.active, formatNumber(country.active))}\n\n" +
                "${getString(R.string.critical, formatNumber(country.critical))}\n"

        alert.setTitle(getString(R.string.today_cases, country.name)).setMessage(msg).show()
    }


    private fun refreshList() {

        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnected) {
            mainViewModel.apply {
                getGlobal()
                getCountries()
            }
            tempList = mutableListOf()
            Log.e(MainActivity::class.simpleName, "In refreshList")
        } else {
            binding.loadingBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_internet)
            binding.emptyState.visibility = View.VISIBLE
            adapter.clear()
            binding.swipeRefresh.isRefreshing = false
        }
    }
}