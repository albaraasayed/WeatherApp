package com.example.kotlinweatherapp.data.remote.datasource

import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): Response<WeatherResponse>

    suspend fun searchCity(
        cityName: String,
        apiKey: String
    ): Response<List<GeocodingResponse>>

    suspend fun reverseGeocode(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Response<List<GeocodingResponse>>
}