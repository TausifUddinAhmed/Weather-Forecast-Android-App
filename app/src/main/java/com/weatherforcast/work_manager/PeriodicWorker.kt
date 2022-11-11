package com.weatherforcast.work_manager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.weatherforcast.PrefManager
import com.weatherforcast.Resource
import com.weatherforcast.application.MainApplication
import com.weatherforcast.db.weather_data.WeatherDataDao
import com.weatherforcast.db.weather_data.WeatherDataDbEntity
import com.weatherforcast.repository.remote.Repository
import com.weatherforcast.utils.Constant
import com.weatherforcast.utils.isOnline
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltWorker
class PeriodicWorker @AssistedInject constructor(
    @Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
    val repository: Repository
): Worker(appContext, workerParams){


    private val weatherDataDao: WeatherDataDao = (applicationContext as MainApplication).database.dao()
    var weatherDataList : MutableList<WeatherDataDbEntity> = mutableListOf()




    override  fun doWork(): Result {
        try {

            val applicationContext = applicationContext



            Log.e("PeriodicWorker", "  Weather API called")

               GlobalScope.launch {

                   repository.getWeatherForecast(
                      PrefManager.getFloat(Constant.LATITUDE).toDouble(),
                      PrefManager.getFloat(Constant.LONGITUDE).toDouble()
                   ).collectLatest {
                       when (it) {
                           is Resource.Success -> {


                               PrefManager.updateValue(Constant.CITY, it.response?.city?.name)
                               for( weathearData in it.response?.list!!){

                                   weatherDataList.add(WeatherDataDbEntity(
                                           null,
                                           weathearData.weather!![0]?.main.toString(),
                                           weathearData.main!!.temp.toString(),
                                           weathearData.main.feels_like.toString(),
                                           weathearData.main.humidity.toString(),
                                           weathearData.dt_txt.toString(),
                                           weathearData.weather[0]!!.icon.toString()
                                       )
                                   )

                               }

                               weatherDataDao.deleteAllAndInsert(
                                   weatherDataList)
                               Log.e("PeriodicWorker", "Success")
                           }
                           is Resource.Error -> {

                               Log.e("PeriodicWorker", "Error" + it.errorCode)

                           }

                       }

                   }


               }



            return Result.success()


        } catch (e: Exception) {

            Log.e("UploadWorker", e.printStackTrace().toString())
            return Result.failure()
        }
    }



}
