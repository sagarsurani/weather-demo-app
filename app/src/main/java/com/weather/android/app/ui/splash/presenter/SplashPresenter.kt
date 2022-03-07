package com.weather.android.app.ui.splash.presenter


import com.weather.android.app.base.MVPBasePresenter
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.ui.splash.view.SplashView


class SplashPresenter(private val pref: PreferenceDataHelper?) : MVPBasePresenter<SplashView>() {

    fun onActivityCreated() {
        view.initViews()
    }

    fun checkWhichScreenOpen(permissionIsGranted: Boolean) {
        if (permissionIsGranted && !pref!!.getRecentSearchList().isNullOrEmpty()) {
            view.openWeatherReportScreen()
        } else {
            view.openHomeScreen()
        }
    }
}