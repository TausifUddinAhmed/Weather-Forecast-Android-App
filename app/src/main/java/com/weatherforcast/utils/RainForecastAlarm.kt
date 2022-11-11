package com.weatherforcast.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.weatherforcast.alert_rain_forecast.RainForecastBroadcastNotification
import com.weatherforcast.db.weather_data.WeatherDataDbEntity
import com.weatherforcast.model.ForecastResponse
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/*fun setRainForecast(context: Context, alertId: Int) {

    val getTime = getTimeInMiliSeconds(2)

    val intent = Intent(context, RainForecastBroadcastNotification::class.java)
    intent.putExtra("alertId", alertId)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alertId,
        intent,
        PendingIntent.FLAG_MUTABLE
    )
    val alarm = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTime, pendingIntent)
    Log.e("RainForeCast", "alarm start")

}

fun getTimeInMiliSeconds(minute: Int?): Long {
    val calendar = getCurrentTime()
    calendar.add(Calendar.MINUTE, minute!!)
    return calendar.timeInMillis
}
*/

fun getCurrentTime(): Calendar {
    val sdf = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss")
    val currentDateandTime: String = sdf.format(Date())

    val date: Date = sdf.parse(currentDateandTime)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

fun checkAndSetRainForecast (  context: Context, forecastResponse: ForecastResponse){
    for (weathearData in forecastResponse.list!!) {
        if(getCurrentTime().timeInMillis >= weathearData.dt!! && weathearData.weather!![0]?.main.toString() =="Rain" ){
           weathearData.dt = weathearData.dt!!- 300000
            setRainForecastV2(context,weathearData.dt!!, 1)
            Log.e("alertRain", weathearData.dt.toString() )
            Log.e("alertRain", weathearData.dt_txt.toString() )
            break
       }
    }
}


fun setRainForecastV2(context: Context, time : Long, alertId: Int) {


    val intent = Intent(context, RainForecastBroadcastNotification::class.java)
    intent.putExtra("alertId", alertId)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alertId,
        intent,
        PendingIntent.FLAG_MUTABLE
    )
    val alarm = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    Log.e("RainForeCast", "alarm start")

}