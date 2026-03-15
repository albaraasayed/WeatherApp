package com.example.kotlinweatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlinweatherapp.data.local.CachedWeather

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getCachedWeather(id: String): CachedWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(weather: CachedWeather)
}