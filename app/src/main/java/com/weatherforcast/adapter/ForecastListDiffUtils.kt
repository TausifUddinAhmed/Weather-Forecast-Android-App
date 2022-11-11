package com.weatherforcast.adapter

import androidx.recyclerview.widget.DiffUtil
import com.weatherforcast.db.weather_data.WeatherDataDbEntity

class ForecastListDiffUtils(
    private val oldItemList:List<WeatherDataDbEntity>,
    private val newItemList: List<WeatherDataDbEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItemList.size
    }

    override fun getNewListSize(): Int {
        return newItemList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemList[oldItemPosition].date== newItemList[newItemPosition].date
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldItemList[oldItemPosition] != newItemList[newItemPosition] -> {
                false
            }

            else -> true
        }
    }
}