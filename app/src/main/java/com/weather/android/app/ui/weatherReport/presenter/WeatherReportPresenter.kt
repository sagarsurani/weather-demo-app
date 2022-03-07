package com.weather.android.app.ui.weatherReport.presenter


import com.weather.android.app.R
import com.weather.android.app.base.MVPBasePresenter
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.models.WeatherReport
import com.weather.android.app.data.models.WeatherReportData
import com.weather.android.app.data.remote.ApiService
import com.weather.android.app.ui.weatherReport.view.WeatherReportView
import com.weather.android.app.utils.Constants
import com.weather.android.app.utils.ConverterUtils
import com.weather.android.app.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherReportPresenter(
    private val apiService: ApiService
) : MVPBasePresenter<WeatherReportView>() {


    fun onActivityCreated() {
        view.initViews()
    }

    fun getWeatherReport(lat: Double, lon: Double) {
        view.showProgressBar(true)
        apiService.getWeatherReport(
            lat.toString(),
            lon.toString(),
            Constants.EXCLUDE,
            Constants.APP_ID_KEY
        )?.enqueue(object : Callback<WeatherReport> {
            override fun onResponse(call: Call<WeatherReport>, response: Response<WeatherReport>) {
                if (response.body() != null && response.isSuccessful) {
                    view.updateData(prepareWeatherDataList(response.body()!!))
                }
                view.showProgressBar(false)
            }

            override fun onFailure(call: Call<WeatherReport>, t: Throwable) {
                view.showMessage(R.string.internet_is_not_available)
                view.showProgressBar(false)
            }
        })
    }

    private fun prepareWeatherDataList(reportData: WeatherReport): ArrayList<WeatherReportData> {
        val list = ArrayList<WeatherReportData>()
        for (i in 0 until reportData.daily.size) {
            list.add(
                WeatherReportData(
                    DateUtils.getDate(reportData.daily[i].dt.toString()),
                    DateUtils.getDayOfWeek(reportData.daily[i].dt.toString()),
                    if (i == 0)
                        ConverterUtils.kelvinToCelsius(reportData.current.temp)
                    else
                        ConverterUtils.kelvinToCelsius(reportData.daily[i].temp.day),
                    ConverterUtils.kelvinToCelsius(reportData.daily[i].temp.min),
                    ConverterUtils.kelvinToCelsius(reportData.daily[i].temp.max),
                    if (i == 0) reportData.current.weather[0].description else reportData.daily[i].weather[0].description
                )
            )
        }
        return list
    }

}