package com.example.kotlinweatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WeatherColorScheme = lightColorScheme(
    primary        = WeatherNavy,
    secondary      = WeatherBlue,
    background     = WeatherBackground,
    surface        = WeatherWhite,
    onPrimary      = WeatherWhite,
    onBackground   = WeatherTextPrimary,
    onSurface      = WeatherTextPrimary,
)

@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WeatherColorScheme,
        content = content
    )
}