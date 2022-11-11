package com.weatherforcast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weatherforcast.db.weather_data.WeatherDataDao
import com.weatherforcast.db.weather_data.WeatherDataDbEntity

@Database(

    entities = [WeatherDataDbEntity::class],
    version = 2
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): WeatherDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }

}