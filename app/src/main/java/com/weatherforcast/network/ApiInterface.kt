package com.weatherforcast.network

import com.weatherforcast.model.ForecastResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {


    @GET(WeatherApiEndPoint.GET_WEATHER_FORCAST)
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
      ): Response<ForecastResponse>

}