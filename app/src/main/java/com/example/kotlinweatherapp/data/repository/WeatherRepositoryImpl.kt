package com.example.kotlinweatherapp.data.repository

import com.example.kotlinweatherapp.data.remote.WeatherApiService
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import com.example.kotlinweatherapp.utils.Constants
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override fun getForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getWeatherForecast(
                lat = lat, lon = lon, apiKey = Constants.API_KEY, units = units, lang = lang
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(Resource.Success(body))
            } else {
                emit(Resource.Error(response.message() ?: "An unknown error occurred"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server. Please check your internet connection."))
        }
    }

    override fun searchCity(query: String): Flow<Resource<List<GeocodingResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchCity(cityName = query, apiKey = Constants.API_KEY)
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
}