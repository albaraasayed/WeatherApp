package com.example.kotlinweatherapp.data.repository

import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.data.local.CachedWeather
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.datasource.WeatherLocalDataSource
import com.example.kotlinweatherapp.data.remote.datasource.WeatherRemoteDataSource
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import com.example.kotlinweatherapp.utils.Constants
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        val latKey = String.format(Locale.ENGLISH, "%.2f", lat)
        val lonKey = String.format(Locale.ENGLISH, "%.2f", lon)
        val cacheId = "${latKey}_${lonKey}"

        val cachedData = localDataSource.getCachedWeather(cacheId)

        if (cachedData != null) {
            emit(Resource.Success(cachedData.weatherData))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = remoteDataSource.getForecast(
                lat = lat, lon = lon, apiKey = Constants.API_KEY, units = units, lang = lang
            )
            val body = response.body()

            if (response.isSuccessful && body != null) {
                localDataSource.insertCachedWeather(CachedWeather(cacheId, body))
                emit(Resource.Success(body))
            } else if (cachedData == null) {
                emit(Resource.Error(response.message() ?: "An unknown error occurred"))
            }
        } catch (e: Exception) {
            if (cachedData == null) {
                val errorMsg = when (e) {
                    is IOException -> "No internet connection."
                    is HttpException -> "Server error."
                    else -> "Something went wrong."
                }
                emit(Resource.Error(errorMsg))
            }
        }
    }

    override fun searchCity(
        query: String
    ): Flow<Resource<List<GeocodingResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.searchCity(cityName = query, apiKey = Constants.API_KEY)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(Resource.Success(body))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                emit(Resource.Error("API Error: $errorMsg"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("App Error: ${e.localizedMessage}"))
        }
    }

    override fun reverseGeocode(lat: Double, lon: Double): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = remoteDataSource.reverseGeocode(lat, lon, Constants.API_KEY)
            val body = response.body()
            if (response.isSuccessful && !body.isNullOrEmpty()) {
                val city = body[0]
                val fullName = "${city.name}, ${city.country}"
                emit(Resource.Success(fullName))
            } else {
                emit(Resource.Error("Location not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getAllFavorites(): Flow<List<FavoriteLocation>> = localDataSource.getAllFavorites()
    suspend fun insertFavorite(location: FavoriteLocation) =
        localDataSource.insertFavorite(location)

    suspend fun deleteFavorite(location: FavoriteLocation) =
        localDataSource.deleteFavorite(location)

    fun getAllAlerts(): Flow<List<AlertEntity>> = localDataSource.getAllAlerts()
    suspend fun insertAlert(alert: AlertEntity): Long = localDataSource.insertAlert(alert)
    suspend fun updateAlert(alert: AlertEntity) = localDataSource.updateAlert(alert)
    suspend fun deleteAlert(alert: AlertEntity) = localDataSource.deleteAlert(alert)
}