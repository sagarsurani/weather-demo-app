package com.weather.android.app.data.remote

import com.weather.android.app.data.models.WeatherReport
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("data/2.5/onecall")
    fun getWeatherReport(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appid: String
    ): Call<WeatherReport>?
}