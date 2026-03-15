package com.example.kotlinweatherapp.data.local.datasource

import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.data.local.CachedWeather
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.dao.AlertDao
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.local.dao.WeatherCacheDao
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    // Cache
    suspend fun getCachedWeather(id: String): CachedWeather?
    suspend fun insertCachedWeather(weather: CachedWeather)

    // Favorites
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(location: FavoriteLocation)
    suspend fun deleteFavorite(location: FavoriteLocation)

    // Alerts
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity): Long
    suspend fun updateAlert(alert: AlertEntity)
    suspend fun deleteAlert(alert: AlertEntity)
}
