package com.asemlab.coronatracker.models

class Country(val totalCases:Long, val newCases:Long, val totalDeaths:Long, val newDeaths:Long,
              val totalRecovered:Long,
              val active:Long,
              val name:String)
