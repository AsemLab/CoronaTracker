package com.asem.coronatracker

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.asem.coronatracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<MutableList<Country>> {

    lateinit var adapter: CountryAdapter
    lateinit var list: ListView
    lateinit var tempList: MutableList<Country>
    lateinit var binding: ActivityMainBinding
    lateinit var connectManager: ConnectivityManager
    lateinit var alert: AlertDialog.Builder
    var connectInfo: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        adapter = CountryAdapter(this, R.layout.item_layout, mutableListOf())
        list = findViewById(R.id.list_view)
        list.adapter = adapter

        alert = AlertDialog.Builder(this)

        list.setOnItemClickListener { parent, view, position, id ->

            startAlert(adapter.objects[position])
        }
        connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnectedOrConnecting) {
            supportLoaderManager.initLoader(1, null, this).forceLoad()
        } else {
            binding.loadingBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_internet)
        }

        tempList = mutableListOf<Country>()

        val search: SearchView = findViewById(R.id.search_bar)
        search.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(qu: String?): Boolean {
                    adapter.clear()
                    adapter.addAll(tempList.filter {
                        it.name.contains(qu?.subSequence(0, qu.length - 1) ?: "", true)
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
        binding.swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_dark,
            android.R.color.holo_red_dark,
            android.R.color.holo_green_dark)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<MutableList<Country>> {
        Log.i(MainActivity::class.simpleName, "In OnCreateLoader")
        return CountryLoader(this)
    }

    override fun onLoadFinished(p0: Loader<MutableList<Country>>, p1: MutableList<Country>?) {

        binding.loadingBar.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        if (p1 == null || p1.isEmpty()) {
            binding.searchBar.visibility = View.GONE
            binding.emptyState.text = getString(R.string.no_data)
            binding.swipeRefresh.isRefreshing = false
            return
        }

        adapter.clear()
        tempList.clear()
        p1.forEach {
            adapter.add(it)
            tempList.add(it)
        }

        generateGlobal()
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onLoaderReset(p0: Loader<MutableList<Country>>) {
        adapter.clear()
    }

    fun startAlert(country: Country){
        val msg: String =
            "\n"+ getString(R.string.confirmed) +"  "+ CountryAdapter.formatNuumbers(country.newCases) + "\n\n" +
                    getString(R.string.deaths) + "  "+CountryAdapter.formatNuumbers(country.newDeaths) + "\n\n" +
                    getString(R.string.active) +"  "+ CountryAdapter.formatNuumbers(country.active) + "\n"

        alert.setTitle(getString(R.string.today_cases)+" "+country.name).setMessage(msg).show()
    }

    fun generateGlobal() {



        val totalCases = findViewById<TextView>(R.id.globalCases_textView)
        totalCases?.text = CountryAdapter.formatNuumbers(NetworkUtils.global.totalCases)

        val totalDeaths = findViewById<TextView>(R.id.globalDeaths_textView)
        totalDeaths?.text = CountryAdapter.formatNuumbers(NetworkUtils.global.totalDeaths)

        val totalRecovered = findViewById<TextView>(R.id.globalRecovered_textView)
        totalRecovered?.text = CountryAdapter.formatNuumbers(NetworkUtils.global.totalRecovered)

        val name = findViewById<TextView>(R.id.globalName_textView)
        name?.text = NetworkUtils.global.name.toString()

        val rip = findViewById<ImageView>(R.id.globalDeaths_imageView)
        rip.background = getDrawable(R.drawable.headstone)

        val hospital = findViewById<ImageView>(R.id.globalRecovered_imageView)
        hospital.background = getDrawable(R.drawable.heart)

        val cases = findViewById<ImageView>(R.id.globalCases_imageView)
        cases.background = getDrawable(R.drawable.mask)

    }

    fun refreshList() {

        connectInfo = connectManager.activeNetworkInfo

        if (connectInfo != null && connectInfo!!.isConnected) {
            supportLoaderManager.destroyLoader(1)
            supportLoaderManager.initLoader(1, null, this).forceLoad()
            tempList = mutableListOf<Country>()
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
        startAlert(NetworkUtils.global)
    }
}