package com.weatherforcast.view_model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {

    private val checkLocationPermissionAndGetLocationDataMutableData: MutableLiveData<Boolean> = MutableLiveData()
    val checkLocationPermissionAndGetLocationDataLiveData: LiveData<Boolean> get() = checkLocationPermissionAndGetLocationDataMutableData


    private val locationMutableLiveData: MutableLiveData<Location> = MutableLiveData()
    val getLocationUpdateLiveData: LiveData<Location> get() = locationMutableLiveData



    fun checkLocationPermissionAndGetLocationData(value: Boolean) {
        checkLocationPermissionAndGetLocationDataMutableData.value = value
    }

    fun sendLocationDataToFragment(value: Location) {
        locationMutableLiveData.value = value
    }


}