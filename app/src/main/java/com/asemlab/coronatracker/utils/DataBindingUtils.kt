package com.asemlab.coronatracker.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.NumberFormat
import java.util.*


@BindingAdapter(value = ["format_numbers"])
fun TextView.formatNumber(number: Long){
    val numberFormat = NumberFormat.getInstance(Locale("sk", "SK"))
    numberFormat.isGroupingUsed = true

    text =  numberFormat.format(number)
}