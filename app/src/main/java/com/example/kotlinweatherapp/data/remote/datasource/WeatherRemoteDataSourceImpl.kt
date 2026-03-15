package com.example.kotlinweatherapp.data.remote.datasource

import com.example.kotlinweatherapp.data.remote.WeatherApiService
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import retrofit2.Response

class WeatherRemoteDataSourceImpl(
    private val apiService: WeatherApiService
) : WeatherRemoteDataSource {
    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> = apiService.getWeatherForecast(lat, lon, apiKey, units, lang)

    override suspend fun searchCity(
        cityName: String,
        apiKey: String
    ): Response<List<GeocodingResponse>> = apiService.searchCity(cityName, apiKey = apiKey)

    override suspend fun reverseGeocode(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Response<List<GeocodingResponse>> = apiService.reverseGeocode(lat, lon, apiKey = apiKey)
}