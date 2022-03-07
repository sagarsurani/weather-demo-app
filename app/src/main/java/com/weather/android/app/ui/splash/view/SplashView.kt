package com.weather.android.app.ui.splash.view

import com.weather.android.app.base.MVPBaseView
import com.weather.android.app.ui.splash.presenter.SplashPresenter

interface SplashView : MVPBaseView<SplashPresenter> {
    fun initViews()
    fun openHomeScreen()
    fun openWeatherReportScreen()
}