package com.example.kotlinweatherapp.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Default coordinates (e.g., Cairo/Alexandria) for testing
        loadWeather(lat = 31.2001, lon = 29.9187)
    }

    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getForecast(lat = lat, lon = lon, units = "metric", lang = "en").collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }
                    is Resource.Success -> {
                        val response = result.data
                        if (response != null && response.list.isNotEmpty()) {
                            val current = response.list[0]

                            val weatherData = WeatherDataUi(
                                city = response.city.name,
                                date = formatDateFull(current.dt_txt),
                                time = formatTime(current.dt_txt),
                                temperature = current.main.temp.toInt(),
                                condition = current.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                                iconUrl = getIconUrl(current.weather.firstOrNull()?.icon, isLarge = true),
                                humidity = current.main.humidity,
                                windSpeed = current.wind.speed,
                                pressure = current.main.pressure,
                                clouds = current.clouds.all,

                                hourlyForecast = response.list.take(8).map { item ->
                                    HourlyForecastUi(
                                        time = formatTime(item.dt_txt),
                                        temperature = item.main.temp.toInt(),
                                        iconUrl = getIconUrl(item.weather.firstOrNull()?.icon)
                                    )
                                },

                                dailyForecast = response.list.filter { it.dt_txt.contains("12:00:00") }.map { item ->
                                    DailyForecastUi(
                                        day = getDayOfWeek(item.dt_txt),
                                        date = formatDateShort(item.dt_txt),
                                        highTemp = item.main.temp_max.toInt(),
                                        lowTemp = item.main.temp_min.toInt(),
                                        iconUrl = getIconUrl(item.weather.firstOrNull()?.icon)
                                    )
                                }
                            )
                            _uiState.value = HomeUiState.Success(weatherData)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    // --- Helpers ---
    private fun getIconUrl(iconCode: String?, isLarge: Boolean = false): String {
        val size = if (isLarge) "@4x" else "@2x"
        return "https://openweathermap.org/img/wn/${iconCode ?: "01d"}$size.png"
    }

    private fun formatTime(dtTxt: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        outFormat.format(inFormat.parse(dtTxt)!!)
    } catch (e: Exception) { dtTxt }

    private fun formatDateFull(dtTxt: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        outFormat.format(inFormat.parse(dtTxt)!!)
    } catch (e: Exception) { dtTxt }

    private fun formatDateShort(dtTxt: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
        outFormat.format(inFormat.parse(dtTxt)!!)
    } catch (e: Exception) { dtTxt }

    private fun getDayOfWeek(dtTxt: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        outFormat.format(inFormat.parse(dtTxt)!!)
    } catch (e: Exception) { dtTxt }
}