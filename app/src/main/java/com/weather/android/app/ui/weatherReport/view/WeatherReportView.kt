package com.weather.android.app.ui.weatherReport.view

import com.weather.android.app.base.MVPBaseView
import com.weather.android.app.data.models.WeatherReportData
import com.weather.android.app.ui.weatherReport.presenter.WeatherReportPresenter
import java.util.ArrayList

interface WeatherReportView : MVPBaseView<WeatherReportPresenter> {
    fun initViews()
    fun updateData(weatherDataList: ArrayList<WeatherReportData>)
    fun showMessage(message: Int)
    fun showProgressBar(isShow: Boolean)
}