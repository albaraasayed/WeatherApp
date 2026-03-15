package com.example.kotlinweatherapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType // 🌟 ADDED IMPORT
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument // 🌟 ADDED IMPORT
import com.example.kotlinweatherapp.presentation.features.alerts.AlertsScreen
import com.example.kotlinweatherapp.presentation.features.alerts.AlertsViewModel
import com.example.kotlinweatherapp.presentation.features.favorites.views.FavoriteDetailsScreen // Ensure this is created
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoriteDetailsViewModel // Ensure this is created
import com.example.kotlinweatherapp.presentation.features.favorites.views.FavoritesScreen
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoritesViewModel
import com.example.kotlinweatherapp.presentation.features.home.HomeScreen
import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import com.example.kotlinweatherapp.presentation.features.settings.SettingsScreen
import com.example.kotlinweatherapp.presentation.features.settings.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    alertsViewModel: AlertsViewModel,
    settingsViewModel: SettingsViewModel,
    favoriteDetailsViewModel: FavoriteDetailsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(viewModel = homeViewModel, navController = navController)
        }
        composable(Routes.Favorites.route) {
            FavoritesScreen(viewModel = favoritesViewModel, navController = navController)
        }
        composable(Routes.Alerts.route) {
            AlertsScreen(viewModel = alertsViewModel, navController = navController)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(viewModel = settingsViewModel, navController = navController)
        }

        composable(
            route = Routes.FavoriteDetails.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("cityName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""

            FavoriteDetailsScreen(
                viewModel = favoriteDetailsViewModel,
                lat = lat,
                lon = lon,
                cityName = cityName,
                navController = navController
            )
        }
    }
}