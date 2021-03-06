package com.asemlab.coronatracker

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

class CountryLoader(context: Context) : AsyncTaskLoader<MutableList<Country>>(context) {
    val url = "https://coronavirus-19-api.herokuapp.com/countries"

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }


    override fun loadInBackground(): MutableList<Country>? {
        val response = NetworkUtils.connect(url)
        Thread.sleep(1000)
        return NetworkUtils.parseResponse(response)
    }

}