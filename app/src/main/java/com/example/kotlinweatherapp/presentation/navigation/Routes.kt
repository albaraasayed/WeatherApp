package com.example.kotlinweatherapp.presentation.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Favorites : Routes("favorites")
    object Alerts : Routes("alerts")
    object Settings : Routes("settings")
    object FavoriteDetails : Routes("favorite_details/{lat}/{lon}/{cityName}") {
        fun createRoute(lat: Double, lon: Double, cityName: String): String {
            return "favorite_details/$lat/$lon/$cityName"
        }
    }
}