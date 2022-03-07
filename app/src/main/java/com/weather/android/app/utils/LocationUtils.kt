package com.weather.android.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.weather.android.app.R
import java.io.IOException
import java.util.*

@SuppressLint("SimpleDateFormat")
class LocationUtils {
    companion object {
        fun getLatLngFromCityOrPostalCode(locationName: String, context: Context): LatLng? {
            var latLng: LatLng? = LatLng(0.00, 0.00)
            if (Geocoder.isPresent()) {
                try {
                    val gc = Geocoder(context)
                    val addresses: List<Address> = gc.getFromLocationName(locationName, 5)
                    if (!addresses.isNullOrEmpty()){
                    val address = addresses[0]
                    latLng = LatLng(address.latitude, address.longitude)
                }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return latLng
            }
            return latLng
        }


        fun getCityNameFromLatLng(lat: Double, lon: Double, context: Context): String? {
            return try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
                addresses[0].locality
            } catch (e: IOException) {
                context.getString(R.string.your_location)
            }
        }
    }
}

