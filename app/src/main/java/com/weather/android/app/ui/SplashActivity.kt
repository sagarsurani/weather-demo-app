package com.weather.android.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.weather.android.app.R
import com.weather.android.app.data.local.prefs.PreferenceDataHelper

class SplashActivity : AppCompatActivity() {
    private var preferenceDataHelper: PreferenceDataHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        preferenceDataHelper = PreferenceDataHelper.getInstance(this)
        Handler(Looper.getMainLooper()).postDelayed({
            if (permissionIsGranted() && !preferenceDataHelper!!.getRecentSearchList().isNullOrEmpty()){
                WeatherReportActivity.start(
                    this,
                    preferenceDataHelper!!.getRecentSearchList()[0].lat!!,
                    preferenceDataHelper!!.getRecentSearchList()[0].lat!!,
                    true
                )
            }else {
                HomeActivity.start(this)
            }
        }, 3000)
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

}