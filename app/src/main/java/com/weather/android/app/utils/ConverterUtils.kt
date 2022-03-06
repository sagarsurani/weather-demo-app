package com.weather.android.app.utils

import android.annotation.SuppressLint

@SuppressLint("SimpleDateFormat")
class ConverterUtils {
    companion object {
        fun kelvinToCelsius(kelvin: Double): String {
            val celsius = String.format("%.2f", (kelvin - 273.15))
            return "$celsius °C"
        }
    }
}

