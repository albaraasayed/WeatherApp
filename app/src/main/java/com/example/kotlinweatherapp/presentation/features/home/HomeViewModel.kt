package com.example.kotlinweatherapp.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var currentLat: Double = 31.2001
    var currentLon: Double = 29.9187

    fun loadWeather(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val units = settingsRepository.tempUnitPrefFlow.first()
            val lang = settingsRepository.langPrefFlow.first()

            repository.getForecast(lat = lat, lon = lon, units = units, lang = lang)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val response = result.data
                            if (response != null && response.list.isNotEmpty()) {
                                val current = response.list[0]
                                val weatherData = WeatherDataUi(
                                    city = response.city.name,
                                    date = formatDateFull(current.dt_txt, lang),
                                    time = formatTime(current.dt_txt, lang),
                                    temperature = current.main.temp.toInt(),
                                    condition = current.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                                        ?: "Unknown",
                                    iconUrl = getIconUrl(
                                        current.weather.firstOrNull()?.icon,
                                        isLarge = true
                                    ),
                                    humidity = current.main.humidity,
                                    windSpeed = current.wind.speed,
                                    pressure = current.main.pressure,
                                    clouds = current.clouds.all,
                                    hourlyForecast = response.list.take(8).map { item ->
                                        HourlyForecastUi(
                                            time = formatTime(item.dt_txt, lang),
                                            temperature = item.main.temp.toInt(),
                                            iconUrl = getIconUrl(item.weather.firstOrNull()?.icon)
                                        )
                                    },
                                    dailyForecast = response.list.filter { it.dt_txt.contains("12:00:00") }
                                        .map { item ->
                                            DailyForecastUi(
                                                day = getDayOfWeek(item.dt_txt, lang),
                                                date = formatDateShort(item.dt_txt, lang),
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

    private fun getIconUrl(iconCode: String?, isLarge: Boolean = false): String {
        val size = if (isLarge) "@4x" else "@2x"
        return "https://openweathermap.org/img/wn/${iconCode ?: "01d"}$size.png"
    }

    private fun getLocale(lang: String): Locale = if (lang == "ar") Locale("ar") else Locale.ENGLISH

    // 🌟 NULL-SAFE FORMATTERS 🌟
    private fun formatTime(dtTxt: String, lang: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("hh:mm a", getLocale(lang))
        inFormat.parse(dtTxt)?.let { outFormat.format(it) } ?: dtTxt
    } catch (e: Exception) {
        dtTxt
    }

    private fun formatDateFull(dtTxt: String, lang: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", getLocale(lang))
        inFormat.parse(dtTxt)?.let { outFormat.format(it) } ?: dtTxt
    } catch (e: Exception) {
        dtTxt
    }

    private fun formatDateShort(dtTxt: String, lang: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("MMM d", getLocale(lang))
        inFormat.parse(dtTxt)?.let { outFormat.format(it) } ?: dtTxt
    } catch (e: Exception) {
        dtTxt
    }

    private fun getDayOfWeek(dtTxt: String, lang: String): String = try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outFormat = SimpleDateFormat("EEE", getLocale(lang))
        inFormat.parse(dtTxt)?.let { outFormat.format(it) } ?: dtTxt
    } catch (e: Exception) {
        dtTxt
    }
}