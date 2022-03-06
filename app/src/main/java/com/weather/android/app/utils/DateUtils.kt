package com.weather.android.app.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateUtils {
    companion object {
        fun getDate(timeStamp: String): String {
            val dv: Long =
            java.lang.Long.valueOf(timeStamp) * 1000
            val df = Date(dv)
            return SimpleDateFormat("MMMM dd, yyyy").format(df)
        }

        fun getDayOfWeek(timeStamp: String): String {
            val dv: Long = java.lang.Long.valueOf(timeStamp) * 1000
            val df = Date(dv)
            var days = arrayOf("Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            return days[df.day]
        }
    }
}

