package com.weatherforcast.repository.remote


import com.weatherforcast.network.ApiInterface
import com.weatherforcast.network.result

class Repository(private val apiInterface: ApiInterface) {


    fun getWeatherForecast( latitude : Double, longitude : Double) = result {
        apiInterface.getWeatherForecast(
            latitude,
            longitude)
    }


}