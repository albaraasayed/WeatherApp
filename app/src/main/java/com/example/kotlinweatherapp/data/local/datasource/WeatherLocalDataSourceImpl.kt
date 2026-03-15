package com.example.kotlinweatherapp.data.local.datasource

import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.data.local.CachedWeather
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.dao.AlertDao
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.local.dao.WeatherCacheDao
import kotlinx.coroutines.flow.Flow


class WeatherLocalDataSourceImpl(
    private val cacheDao: WeatherCacheDao,
    private val favoriteDao: FavoriteLocationDao,
    private val alertDao: AlertDao
) : WeatherLocalDataSource {
    override suspend fun getCachedWeather(id: String): CachedWeather? =
        cacheDao.getCachedWeather(id)

    override suspend fun insertCachedWeather(weather: CachedWeather) =
        cacheDao.insertCachedWeather(weather)

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> =
        favoriteDao.getAllFavorites()

    override suspend fun insertFavorite(location: FavoriteLocation) =
        favoriteDao.insertFavorite(location)

    override suspend fun deleteFavorite(location: FavoriteLocation) =
        favoriteDao.deleteFavorite(location)

    override fun getAllAlerts(): Flow<List<AlertEntity>> =
        alertDao.getAllAlerts()

    override suspend fun insertAlert(alert: AlertEntity): Long =
        alertDao.insertAlert(alert)

    override suspend fun updateAlert(alert: AlertEntity) =
        alertDao.updateAlert(alert)

    override suspend fun deleteAlert(alert: AlertEntity) =
        alertDao.deleteAlert(alert)
}