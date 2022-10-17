package com.asemlab.coronatracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asemlab.coronatracker.models.Country
import com.asemlab.coronatracker.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var apiService: ApiService) : ViewModel() {


    val countries = MutableLiveData<List<Country>>()

    fun getCountries() {
        runBlocking {
            withContext(Dispatchers.IO) {
                val c = apiService.getCountries().execute()
                if (c.isSuccessful)
                    countries.postValue(c.body())
            }
        }
    }
}