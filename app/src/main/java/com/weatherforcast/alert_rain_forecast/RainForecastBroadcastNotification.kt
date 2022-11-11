package com.weatherforcast.alert_rain_forecast

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.weatherforcast.R
import com.weatherforcast.utils.Constant
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi

@AndroidEntryPoint
class RainForecastBroadcastNotification @AssistedInject constructor() : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")


    override fun onReceive(context: Context?, intent: Intent?) {

        Log.e("RainForeCast", "onReceive called")
        val alertCode = intent!!.extras!!.getInt("alertId")

        val intent = Intent(Constant.RAIN_NOTIFICATION)
        intent.putExtra("alertId", alertCode)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

        showNotification(context,"Alert", "Rain is coming !")

    }

    fun showNotification( context: Context?, title: String, content: String, intent: PendingIntent? = null) {
        val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context,"123")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setAutoCancel(true)
            .setDefaults(Notification.FLAG_AUTO_CANCEL or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (intent != null) {
            builder.setContentIntent(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Forecast"
            val descriptionText = "Rain Forecast"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("123", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }

    }


}