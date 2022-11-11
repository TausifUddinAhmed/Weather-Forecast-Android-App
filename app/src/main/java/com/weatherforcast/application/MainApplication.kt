package com.weatherforcast.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.weatherforcast.PrefManager
import com.weatherforcast.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        context = applicationContext
        PrefManager.setInstance(this)
        WorkManager.initialize(this, workManagerConfiguration)

    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()



    companion object {
        lateinit var context: Context
    }


}