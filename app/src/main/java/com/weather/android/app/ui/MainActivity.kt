package com.weather.android.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.weather.android.app.R
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.data.models.RecentSearchHistory
import com.weather.android.app.ui.adapter.RecentSearchAdapter
import com.weather.android.app.utils.Constants
import com.weather.android.app.utils.GPSUtil
import com.weather.android.app.utils.LocationUtils
import com.weather.android.app.utils.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var preferenceDataHelper: PreferenceDataHelper? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    var mFusedLocationClient: FusedLocationProviderClient? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initView()
    }

    private fun initView() {
        preferenceDataHelper = PreferenceDataHelper.getInstance(this)
        initRecyclerView()
        btnSearch.setOnClickListener { getReportFromSearch() }
        tvCurrentLocation.setOnClickListener { getReportFromCurrentLocation() }
    }

    private fun initRecyclerView() {
//        llRecentSearch.visibility = View.VISIBLE
        rvRecentSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recentSearchAdapter = RecentSearchAdapter(preferenceDataHelper!!.getRecentSearchList(),
            fun(item: RecentSearchHistory) { getReportFromRecentSearch(item) })
        rvRecentSearch.adapter = recentSearchAdapter
    }

    private fun updateSearchHistoryList(list: ArrayList<RecentSearchHistory>) {
        if (this::recentSearchAdapter.isInitialized) {
            recentSearchAdapter.setList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        updateSearchHistoryList(preferenceDataHelper!!.getRecentSearchList())
    }

    private fun getReportFromCurrentLocation() {
        if (permissionIsGranted()) {
            if (currentLatitude != null && currentLongitude != null) {
                addDataInSearchHistory(currentLatitude!!, currentLongitude!!)
                openWeatherReportScreen(currentLatitude!!, currentLongitude!!)
            } else {
                getLastCurrentLocation()
            }
        }
    }

    private fun getReportFromRecentSearch(item: RecentSearchHistory) {
        if (permissionIsGranted()) {
            addDataInSearchHistory(item.lat!!, item.lng!!)
            openWeatherReportScreen(item.lat, item.lng)
        }
    }

    private fun getReportFromSearch() {
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
                openWeatherReportScreen(lat, lon)
            }
        }
    }

    private fun addDataInSearchHistory(lat: Double, lon: Double) {
        val item = RecentSearchHistory(LocationUtils.getCityNameFromLatLng(lat, lon, this)!!, lat, lon)
        for (i in 0 until preferenceDataHelper!!.getRecentSearchList().size){
            if (preferenceDataHelper!!.getRecentSearchList()[i].title==item.title){
                preferenceDataHelper!!.deleteSearchItem(i)
                break
            }
        }
        PreferenceDataHelper.getInstance(this)!!.addSearchItem(item)
    }

    private fun openWeatherReportScreen(latitude: Double, longitude: Double) {
        WeatherReportActivity.start(this@MainActivity, latitude, longitude, false)
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastCurrentLocation() {
        if (GPSUtil.checkGPSStatus(this)) {
            mFusedLocationClient!!.lastLocation.addOnCompleteListener {
                val location: Location = it.result
                if (location == null) {
                    requestNewCurrentLocationData();
                } else {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                }
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
                        this@MainActivity, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    ActivityCompat.requestPermissions(
                        this@MainActivity, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    GPSUtil.buildAlertMessageDenyPermission(
                        this@MainActivity,
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



