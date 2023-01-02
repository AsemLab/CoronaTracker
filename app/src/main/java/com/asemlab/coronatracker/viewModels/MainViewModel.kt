package com.asemlab.coronatracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asemlab.coronatracker.models.Country
import com.asemlab.coronatracker.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var apiService: ApiService) : ViewModel() {

    val countries = MutableLiveData<List<Country>>()
    val global = MutableLiveData<Country>()

    fun getCountries() {
        request(countries) { apiService.getCountries() }
    }

    fun getGlobal() {
        request(global) { apiService.getGlobal() }
    }

    private fun <T> request(data: MutableLiveData<T>, call: () -> Call<T>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val c = call.invoke().execute()
                if (c.isSuccessful)
                    data.postValue(c.body())
            }
        }
    }
}