package com.weather.android.app.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.weather.android.app.R
import com.weather.android.app.base.BaseActivity
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.models.RecentSearchHistory
import com.weather.android.app.ui.weatherReport.view.WeatherReportActivity
import com.weather.android.app.ui.adapter.RecentSearchAdapter
import com.weather.android.app.ui.home.presenter.HomePresenter
import com.weather.android.app.utils.*
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity<HomePresenter>(), HomeView {
    private var preferenceDataHelper: PreferenceDataHelper? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        presenter.onActivityCreated()
    }

    override fun initViews() {
        preferenceDataHelper = PreferenceDataHelper.getInstance(this)
        initRecyclerView()
        btnSearch.setOnClickListener { presenter.getReportBySearchingCityOrPostalCode() }
        tvCurrentLocation.setOnClickListener { presenter.currentLocationWeatherReport() }
    }

    private fun initRecyclerView() {
        rvRecentSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recentSearchAdapter = RecentSearchAdapter(preferenceDataHelper!!.getRecentSearchList(),
            fun(item: RecentSearchHistory) { presenter.recentSearchWeatherReport(item) },
            fun() { showHideRecentView() })
        rvRecentSearch.adapter = recentSearchAdapter
        showHideRecentView()
    }

    private fun showHideRecentView() {
        if (!preferenceDataHelper!!.getRecentSearchList().isNullOrEmpty())
            llRecentSearch.visibility = View.VISIBLE
        else
            llRecentSearch.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (this::recentSearchAdapter.isInitialized) {
            recentSearchAdapter.setList(preferenceDataHelper!!.getRecentSearchList())
            showHideRecentView()
        }
    }

    override fun getReportFromCurrentLocation() {
        if (permissionIsGranted()) {
            if (currentLatitude != null && currentLongitude != null) {
                addDataInSearchHistory(currentLatitude!!, currentLongitude!!)
            } else {
                getLastCurrentLocation()
            }
        }
    }

    override fun getReportFromRecentSearch(item: RecentSearchHistory) {
        if (permissionIsGranted()) {
            addDataInSearchHistory(item.lat!!, item.lng!!)
        }
    }

    override fun getReportFromSearch() {
        if (permissionIsGranted()) {
            val lat = LocationUtils.getLatLngFromCityOrPostalCode(
                etSearchLocation.text.toString(),
                this
            )?.latitude
            val lon = LocationUtils.getLatLngFromCityOrPostalCode(
                etSearchLocation.text.toString(),
                this
            )?.longitude
            if (lat == 0.0 && lon == 0.0) {
                showMessage(getString(R.string.location_not_found))
            } else {
                addDataInSearchHistory(lat!!, lon!!)
            }
        }
    }

    private fun addDataInSearchHistory(lat: Double, lon: Double) {
        if (NetworkUtil.isNetworkConnected(this)) {
            val item = RecentSearchHistory(LocationUtils.getCityNameFromLatLng(lat, lon, this)!!, lat, lon)
            presenter.addItemInSearchHistory(item)
        }
    }

    override fun openWeatherReportScreen(lat: Double?, lng: Double?) {
        WeatherReportActivity.start(this@HomeActivity, lat!!, lng!!, false)
    }

    override fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getNewPresenter(): HomePresenter {
        return HomePresenter(PreferenceDataHelper.getInstance(this)!!)
    }

    @SuppressLint("MissingPermission")
    private fun getLastCurrentLocation() {
        if (GPSUtil.checkGPSStatus(this)) {
            if (NetworkUtil.isNetworkConnected(this)) {
                mFusedLocationClient!!.lastLocation.addOnCompleteListener {
                    if (it.result == null) {
                        requestNewCurrentLocationData()
                    } else {
                        currentLatitude = it.result.latitude
                        currentLongitude = it.result.longitude
                        addDataInSearchHistory(currentLatitude!!, currentLongitude!!)
                    }
                }
            } else {
                showMessage(getString(R.string.internet_is_not_available))
            }
        } else {
            GPSUtil.buildAlertMessageNoGps(this, null).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewCurrentLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            currentLatitude = mLastLocation.latitude
            currentLongitude = mLastLocation.longitude
            addDataInSearchHistory(currentLatitude!!, currentLongitude!!)
        }
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
            checkPermission()
            return false
        }
        return true
    }

    private fun checkPermission() {
        PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION,
            object : PermissionUtil.PermissionAskListener {
                override fun onNeedPermission() {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    GPSUtil.buildAlertMessageDenyPermission(
                        this@HomeActivity,
                        getString(R.string.permission_location_enable_text), null
                    ).show()
                }

                override fun onPermissionGranted() {
                    //
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_SETTINGS) {
            checkPermission()
        }
    }
}



