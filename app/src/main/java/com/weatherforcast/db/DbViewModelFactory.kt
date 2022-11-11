package com.weatherforcast.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weatherforcast.db.weather_data.WeatherDataDao
import com.weatherforcast.db.weather_data.WeatherDataRoomViewModel

class DbViewModelFactory(
    private val weatherDataDao: WeatherDataDao? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDataRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDataRoomViewModel(weatherDataDao!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}