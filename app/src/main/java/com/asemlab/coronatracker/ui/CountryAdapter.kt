package com.asemlab.coronatracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.asemlab.coronatracker.R
import com.asemlab.coronatracker.models.Country
import java.text.NumberFormat
import java.util.*

class CountryAdapter(
    context: Context,
    resource: Int,
    var objects: MutableList<Country>
) : ArrayAdapter<Country>(context, resource, objects) {

    companion object {
        fun formatNuumbers(num: Long): String {
            val n: NumberFormat = NumberFormat.getInstance(Locale("sk", "SK"))
            n.isGroupingUsed = true

            return n.format(num)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        var listView:View = convertView?:LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)

        val current = getItem(position)
        val totalCases = listView.findViewById<TextView>(R.id.totalCases_textView)
        totalCases?.text = current?.totalCases?.let { formatNuumbers(it) }

        val totalDeaths = listView.findViewById<TextView>(R.id.deaths_textView)
        totalDeaths?.text = current?.totalDeaths?.let { formatNuumbers(it) }

        val totalRecovered = listView.findViewById<TextView>(R.id.recovered_textView)
        totalRecovered?.text = current?.totalRecovered?.let { formatNuumbers(it) }

        val name = listView.findViewById<TextView>(R.id.name_textView)
        name?.text = current?.name.toString()
        
        return listView
    }
}