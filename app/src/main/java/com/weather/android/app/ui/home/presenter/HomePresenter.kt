package com.weather.android.app.ui.home.presenter


import com.weather.android.app.base.MVPBasePresenter
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.models.RecentSearchHistory
import com.weather.android.app.ui.home.view.HomeView


class HomePresenter(private val preferenceDataHelper: PreferenceDataHelper) :
    MVPBasePresenter<HomeView>() {


    fun onActivityCreated() {
        view.initViews()
    }

    fun addItemInSearchHistory(item: RecentSearchHistory) {
        for (i in 0 until preferenceDataHelper.getRecentSearchList().size) {
            if (preferenceDataHelper.getRecentSearchList()[i].title == item.title) {
                preferenceDataHelper.deleteSearchItem(i)
                break
            }
        }
        preferenceDataHelper.addSearchItem(item)
        view.openWeatherReportScreen(item.lat,item.lng)
    }

    fun getReportBySearchingCityOrPostalCode() {
        view.getReportFromSearch()
    }

    fun currentLocationWeatherReport() {
        view.getReportFromCurrentLocation()
    }

    fun recentSearchWeatherReport(item: RecentSearchHistory) {
       view.getReportFromRecentSearch(item)
    }
}