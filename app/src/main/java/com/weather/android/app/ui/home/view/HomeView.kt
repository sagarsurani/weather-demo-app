package com.weather.android.app.ui.home.view

import com.weather.android.app.base.MVPBaseView
import com.weather.android.app.data.models.RecentSearchHistory
import com.weather.android.app.ui.home.presenter.HomePresenter

interface HomeView : MVPBaseView<HomePresenter> {
    fun initViews()
    fun showMessage(message: String)
    fun openWeatherReportScreen(lat: Double?, lng: Double?)
    fun getReportFromSearch()
    fun getReportFromCurrentLocation()
    fun getReportFromRecentSearch(item: RecentSearchHistory)
}