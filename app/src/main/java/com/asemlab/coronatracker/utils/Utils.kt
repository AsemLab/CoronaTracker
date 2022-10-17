package com.asemlab.coronatracker.utils

import java.text.NumberFormat
import java.util.*

fun formatNumber(number: Long?): String {
    val numberFormat = NumberFormat.getInstance(Locale("sk", "SK"))
    numberFormat.isGroupingUsed = true

    return numberFormat.format(number)
}