package com.asemlab.coronatracker.services

import com.asemlab.coronatracker.app.BASE_URL
import com.asemlab.coronatracker.models.Country
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("${BASE_URL}countries")
    fun getCountries(): Call<List<Country>>

    @GET("${BASE_URL}all")
    fun getGlobal(): Call<Country>
}