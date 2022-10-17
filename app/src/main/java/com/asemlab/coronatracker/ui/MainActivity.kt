package com.asemlab.coronatracker.ui

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import com.asemlab.coronatracker.R
import com.asemlab.coronatracker.databinding.ActivityMainBinding
import com.asemlab.coronatracker.models.Country
import com.asemlab.coronatracker.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    lateinit var adapter: CountryAdapter
    lateinit var list: ListView
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

        mainViewModel.countries.observe(this){
            addData(it)

        }
        setupAdapter()

        alert = AlertDialog.Builder(this)

        connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnectedOrConnecting) {
            mainViewModel.getCountries()
        } else {
            binding.loadingBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_internet)
        }

        tempList = mutableListOf()

        val search: SearchView = findViewById(R.id.search_bar)
        search.apply {
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
        list = findViewById(R.id.list_view)
        list.adapter = adapter

        list.setOnItemClickListener { parent, view, position, id ->

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
        for( i in 1 until countries.size){
            adapter.add(countries[i])
            tempList.add(countries[i])
        }
        global = countries[0]
        adapter.objects.sortBy {
            it.name
        }
        tempList.sortBy {
            it.name
        }
        generateGlobal()
        binding.swipeRefresh.isRefreshing = false
    }

    private fun startAlert(country: Country) {
        val msg: String =
            "\n" + getString(R.string.confirmed) + "  " + CountryAdapter.formatNumbers(country.newCases) + "\n\n" +
                    getString(R.string.deaths) + "  " + CountryAdapter.formatNumbers(country.newDeaths) + "\n\n" +
                    getString(R.string.active) + "  " + CountryAdapter.formatNumbers(country.active) + "\n"

        alert.setTitle(getString(R.string.today_cases) + " " + country.name).setMessage(msg).show()
    }

    private fun generateGlobal() {


        val totalCases = findViewById<TextView>(R.id.globalCases_textView)
        totalCases?.text = CountryAdapter.formatNumbers(global.totalCases)

        val totalDeaths = findViewById<TextView>(R.id.globalDeaths_textView)
        totalDeaths?.text = CountryAdapter.formatNumbers(global.totalDeaths)

        val totalRecovered = findViewById<TextView>(R.id.globalRecovered_textView)
        totalRecovered?.text = CountryAdapter.formatNumbers(global.totalRecovered)

        val name = findViewById<TextView>(R.id.globalName_textView)
        name?.text = global.name

        val rip = findViewById<ImageView>(R.id.globalDeaths_imageView)
        rip.background = getDrawable(R.drawable.headstone)

        val hospital = findViewById<ImageView>(R.id.globalRecovered_imageView)
        hospital.background = getDrawable(R.drawable.heart)

        val cases = findViewById<ImageView>(R.id.globalCases_imageView)
        cases.background = getDrawable(R.drawable.mask)

    }

    private fun refreshList() {

        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnected) {
            mainViewModel.getCountries()
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

    fun clickOnGlobal(view: View) {
        startAlert(global)
    }
}