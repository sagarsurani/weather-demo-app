package com.weather.android.app.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class ConverterUtils {
    companion object {
        fun kelvinToCelsius(kelvin: Double): String {
            val celsius = String.format("%.2f", (kelvin - 273.15))
            return "$celsius °C"
        }
    }
}

