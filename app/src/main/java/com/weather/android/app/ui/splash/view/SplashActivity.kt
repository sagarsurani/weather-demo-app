package com.weather.android.app.ui.splash.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.weather.android.app.R
import com.weather.android.app.base.BaseActivity
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.ui.home.view.HomeActivity
import com.weather.android.app.ui.splash.presenter.SplashPresenter
import com.weather.android.app.ui.weatherReport.view.WeatherReportActivity

class SplashActivity : BaseActivity<SplashPresenter>(), SplashView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        presenter.onActivityCreated()
    }

    override fun initViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            presenter.checkWhichScreenOpen(permissionIsGranted())
        }, 3000)
    }

    override fun openHomeScreen() {
        HomeActivity.start(this)
    }

    override fun openWeatherReportScreen() {
        WeatherReportActivity.start(
            this,
            PreferenceDataHelper.getInstance(this)!!.getRecentSearchList()[0].lat!!,
            PreferenceDataHelper.getInstance(this)!!.getRecentSearchList()[0].lat!!,
            true
        )
    }

    private fun permissionIsGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    override fun getNewPresenter(): SplashPresenter {
        return SplashPresenter(PreferenceDataHelper.getInstance(this))
    }
}