package com.weather.android.app.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.android.app.R
import com.weather.android.app.ui.adapter.WeatherReportAdapter
import com.weather.android.app.data.models.WeatherReport
import com.weather.android.app.data.models.WeatherReportData
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.remote.ApiService
import com.weather.android.app.data.remote.RemoteServiceHelper
import com.weather.android.app.utils.Constants
import com.weather.android.app.utils.ConverterUtils
import com.weather.android.app.utils.DateUtils
import com.weather.android.app.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_weather_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherReportActivity : AppCompatActivity(), LocationListener {
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

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_report)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, this)
        lat = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
        lon = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
        isFromSplash = intent.getBooleanExtra(Constants.IsFromSplash, false)
        preferenceDataHelper = PreferenceDataHelper.getInstance(this)
        initView()
    }

    private fun initView() {
        tvHeader.text = getString(R.string.weather_header_desc, if (!isFromSplash) LocationUtils.getCityNameFromLatLng(lat!!, lon!!, this) else preferenceDataHelper!!.getRecentSearchList()[0].title)
        initRecyclerView()
        tvChangeAddress.setOnClickListener { onBackPressed() }
        checkLocationPoints()
    }

    override fun onBackPressed() {
        if (isFromSplash) {
            MainActivity.start(this)
        } else {
            super.onBackPressed()
        }
    }

    private fun checkLocationPoints() {
        getWeatherReport(lat!!, lon!!)
    }

    private fun getWeatherReport(lat: Double, lon: Double) {
        showProgressBar(true)
        val apiService = RemoteServiceHelper.getRetrofitInstance(this)!!.create(ApiService::class.java)
        apiService.getWeatherReport(
            lat.toString(),
            lon.toString(),
            Constants.EXCLUDE,
            Constants.APP_ID_KEY
        )?.enqueue(object : Callback<WeatherReport> {
            override fun onResponse(call: Call<WeatherReport>, response: Response<WeatherReport>) {
                if (response.body() != null && response.isSuccessful) {
                    updateData(prepareWeatherDataList(response.body()!!))
                }
                showProgressBar(false)
            }

            override fun onFailure(call: Call<WeatherReport>, t: Throwable) {
                showMessage(getString(R.string.failed_to_show_report))
                showProgressBar(false)
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
                    if (i == 0) ConverterUtils.kelvinToCelsius(reportData.current.temp) else ConverterUtils.kelvinToCelsius(
                        reportData.daily[i].temp.day
                    ),
                    ConverterUtils.kelvinToCelsius(reportData.daily[i].temp.min),
                    ConverterUtils.kelvinToCelsius(reportData.daily[i].temp.max),
                    if (i == 0) reportData.current.weather[0].description else reportData.daily[i].weather[0].description
                )
            )
        }
        return list
    }

    private fun updateData(weatherDataList: ArrayList<WeatherReportData>) {
        if (this::weatherReportAdapter.isInitialized) {
            weatherReportAdapter.setList(weatherDataList)
        }
    }

    private fun showProgressBar(isShow: Boolean) {
        if (isShow) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
}