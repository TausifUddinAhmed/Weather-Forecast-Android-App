package com.weatherforcast.db.weather_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walton.eapp.db.TableName.TABLE_WEATHER_DATA


@Entity(tableName = TABLE_WEATHER_DATA)
data class WeatherDataDbEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val weather_status: String,
    val temp: String,
    val feels_like: String,
    val humidity: String,
    val date: String,
    val image_url: String
){

}