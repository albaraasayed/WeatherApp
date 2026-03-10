package com.example.kotlinweatherapp.data.remote.dto

import com.example.kotlinweatherapp.data.model.City
import com.example.kotlinweatherapp.data.model.WeatherEntry

data class WeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherEntry>,
    val message: Int
)