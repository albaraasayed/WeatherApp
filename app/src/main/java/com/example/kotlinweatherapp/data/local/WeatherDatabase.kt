package com.example.kotlinweatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kotlinweatherapp.data.local.dao.AlertDao
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.local.dao.WeatherCacheDao

@Database(
    entities = [FavoriteLocation::class, AlertEntity::class, CachedWeather::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val favoriteLocationDao: FavoriteLocationDao
    abstract val alertDao: AlertDao
    abstract val weatherCacheDao: WeatherCacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}