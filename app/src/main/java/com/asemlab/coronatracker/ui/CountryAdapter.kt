package com.asemlab.coronatracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.asemlab.coronatracker.databinding.ItemLayoutBinding
import com.asemlab.coronatracker.models.Country

class CountryAdapter(
    context: Context,
    val resource: Int,
    var objects: MutableList<Country>
) : ArrayAdapter<Country>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.countryItem = getItem(position)
        return binding.root
    }
}