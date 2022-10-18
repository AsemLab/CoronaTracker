package com.asemlab.coronatracker.models

import com.google.gson.annotations.SerializedName


data class Country(
    @SerializedName("country")
    val name: String?,
    @SerializedName("cases")
    val totalCases: Long?,
    @SerializedName("todayCases")
    val newCases: Long?,
    @SerializedName("deaths")
    val totalDeaths: Long?,
    @SerializedName("todayDeaths")
    val newDeaths: Long?,
    @SerializedName("recovered")
    val totalRecovered: Long?,
    @SerializedName("active")
    val active: Long?,
    @SerializedName("critical")
    val critical: Long?
)