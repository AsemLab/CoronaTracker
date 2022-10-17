package com.asemlab.coronatracker.utils

import android.util.Log
import com.asemlab.coronatracker.models.Country
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NetworkUtils {

    companion object {

        lateinit var global: Country

        fun connect(url: String): String {

            val http: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            http.apply {
                requestMethod = "GET"
                readTimeout = 10000
                connectTimeout = 15000
                connect()
            }
            val input: InputStream = http.inputStream
            val reader: BufferedReader = BufferedReader(InputStreamReader(input))
            var line: StringBuilder = StringBuilder()
            reader.forEachLine {
                line.append(it + "\n")
            }

            return line.toString()
        }

        fun parseInfo(tempObj: JSONObject): Country {
            try{
                val totalCases = tempObj.optLong("cases")
                val newCases = tempObj.optLong("todayCases")

                val totalDeaths = tempObj.optLong("deaths")
                val newDeaths = tempObj.optLong("todayDeaths")

                val totalRecovered = tempObj.optLong("recovered")
                val active = tempObj.optLong("active")

                val name: String = tempObj.optString("country")

                return Country(totalCases,newCases,totalDeaths,newDeaths,totalRecovered,active,name)

            }catch (e: JSONException){
               Log.e(NetworkUtils::class.simpleName,"In parseInfo")
            }
            return Country(0,0,0,0,0,0,"")
        }

        fun parseResponse(response: String): MutableList<Country> {
            val countries: MutableList<Country> = mutableListOf()
            try {
                val tempObj = JSONArray(response).getJSONObject(0)
                global = parseInfo(tempObj)

                val countryJson = JSONArray(response)

                for (i in 1..countryJson.length()-1) {
                    val tempObj: JSONObject = countryJson.get(i) as JSONObject

                    countries.add(
                        parseInfo(tempObj)
                        )
                }
                countries.sortBy {
                    it.name
                }
            } catch (e: JSONException) {
                Log.e(NetworkUtils::class.simpleName,"In parseResponse " +e.message )
            }

            return countries

        }
    }

}