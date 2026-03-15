package com.example.kotlinweatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import com.google.gson.Gson

@Entity(tableName = "weather_cache")
data class CachedWeather(
    @PrimaryKey val id: String,
    val weatherData: WeatherResponse,
    val timestamp: Long = System.currentTimeMillis()
)

class WeatherTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherResponse(weather: WeatherResponse?): String? {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherResponse(json: String?): WeatherResponse? {
        return gson.fromJson(json, WeatherResponse::class.java)
    }
}