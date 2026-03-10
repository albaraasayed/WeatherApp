package com.example.kotlinweatherapp.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlinweatherapp.presentation.common.*
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherBackground
import com.example.kotlinweatherapp.ui.theme.WeatherNavy

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            WeatherBottomNavBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { selectedNavIndex = it })
        },
        containerColor = WeatherBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = WeatherNavy
                    )
                }

                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = WeatherNavy)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.loadWeather(31.2001, 29.9187)
                        }) { Text("Retry") }
                    }
                }

                is HomeUiState.Success -> {
                    val data = state.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        WeatherHeaderSection(city = data.city, date = data.date, time = data.time)

                        CurrentWeatherHero(
                            temperature = data.temperature,
                            condition = data.condition,
                            iconUrl = data.iconUrl
                        )

                        WeatherStatsCard(
                            humidity = data.humidity,
                            windSpeed = data.windSpeed,
                            pressure = data.pressure,
                            clouds = data.clouds
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        HourlyForecastList(forecasts = data.hourlyForecast)
                        Spacer(modifier = Modifier.height(24.dp))
                        DailyForecastList(forecasts = data.dailyForecast)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPre() {
//    HomeScreen()
}