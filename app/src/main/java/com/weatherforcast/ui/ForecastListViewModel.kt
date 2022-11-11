package com.weatherforcast.ui

import com.weatherforcast.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForecastListViewModel @Inject constructor() : BaseViewModel() {


    fun getWeatherForecast(lat : Double,  lng : Double) = repository.getWeatherForecast(lat,lng)



}