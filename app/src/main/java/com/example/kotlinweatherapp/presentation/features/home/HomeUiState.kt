package com.example.kotlinweatherapp.presentation.features.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: WeatherDataUi) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class WeatherDataUi(
    val city: String,
    val date: String,
    val time: String,
    val temperature: Int,
    val condition: String,
    val iconUrl: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val clouds: Int,
    val hourlyForecast: List<HourlyForecastUi>,
    val dailyForecast: List<DailyForecastUi>
)

data class HourlyForecastUi(
    val time: String,
    val temperature: Int,
    val iconUrl: String
)

data class DailyForecastUi(
    val day: String,
    val date: String,
    val highTemp: Int,
    val lowTemp: Int,
    val iconUrl: String
)