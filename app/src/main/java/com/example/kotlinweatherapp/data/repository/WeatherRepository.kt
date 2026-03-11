package com.example.kotlinweatherapp.data.repository

import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>>

    fun searchCity(query: String): Flow<Resource<List<GeocodingResponse>>>
}