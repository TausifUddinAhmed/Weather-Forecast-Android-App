package com.weatherforcast.utils

import android.content.Context
import android.location.LocationManager


fun checkGPSLocationService(locationManager: LocationManager) : Boolean {
    return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun checkNetworkLocationService(locationManager: LocationManager): Boolean {

    return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

}

