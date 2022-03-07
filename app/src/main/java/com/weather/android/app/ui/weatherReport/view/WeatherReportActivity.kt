package com.weather.android.app.ui.weatherReport.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.android.app.R
import com.weather.android.app.base.BaseActivity
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.models.WeatherReportData
import com.weather.android.app.data.remote.ApiService
import com.weather.android.app.data.remote.RemoteServiceHelper
import com.weather.android.app.ui.adapter.WeatherReportAdapter
import com.weather.android.app.ui.home.view.HomeActivity
import com.weather.android.app.ui.weatherReport.presenter.WeatherReportPresenter
import com.weather.android.app.utils.Constants
import com.weather.android.app.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_weather_report.*


class WeatherReportActivity : BaseActivity<WeatherReportPresenter>(), WeatherReportView, LocationListener {
    private var isFromSplash: Boolean = false
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var weatherReportAdapter: WeatherReportAdapter
    private var preferenceDataHelper: PreferenceDataHelper? = null
    private var locationManager: LocationManager? = null
    private var location: Location? = null

    companion object {
        fun start(context: Context, latitude: Double, longitude: Double, isFromSplash: Boolean) {
            val intent = Intent(context, WeatherReportActivity::class.java)
            intent.putExtra(Constants.LATITUDE, latitude)
            intent.putExtra(Constants.LONGITUDE, longitude)
            intent.putExtra(Constants.IsFromSplash, isFromSplash)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_report)
        presenter.onActivityCreated()
    }

    override fun initViews() {
        tvHeader.text = getString(
            R.string.weather_header_desc,
            if (!isFromSplash) LocationUtils.getCityNameFromLatLng(
                lat!!,
                lon!!,
                this
            ) else preferenceDataHelper!!.getRecentSearchList()[0].title
        )
        initRecyclerView()
        tvChangeAddress.setOnClickListener { onBackPressed() }
        presenter.getWeatherReport(lat!!, lon!!)
    }

    override fun onBackPressed() {
        if (isFromSplash) {
            HomeActivity.start(this)
        } else {
            super.onBackPressed()
        }
    }

    override fun updateData(weatherDataList: ArrayList<WeatherReportData>) {
        if (this::weatherReportAdapter.isInitialized) {
            weatherReportAdapter.setList(weatherDataList)
        }
    }

    override fun showProgressBar(isShow: Boolean) {
        if (isShow) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun showMessage(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        rvWeatherReport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weatherReportAdapter = WeatherReportAdapter()
        rvWeatherReport.adapter = weatherReportAdapter
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
    }

    @SuppressLint("MissingPermission")
    override fun getNewPresenter(): WeatherReportPresenter {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, this)
        lat = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
        lon = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
        isFromSplash = intent.getBooleanExtra(Constants.IsFromSplash, false)
        preferenceDataHelper = PreferenceDataHelper.getInstance(this)
        return WeatherReportPresenter(RemoteServiceHelper.getRetrofitInstance(this)!!.create(ApiService::class.java))
    }
}