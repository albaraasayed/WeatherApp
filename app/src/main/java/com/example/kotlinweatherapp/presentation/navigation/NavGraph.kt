package com.example.kotlinweatherapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlinweatherapp.presentation.features.favorites.FavoritesScreen
import com.example.kotlinweatherapp.presentation.features.favorites.FavoritesViewModel
import com.example.kotlinweatherapp.presentation.features.home.HomeScreen
import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import androidx.compose.ui.res.stringResource
import com.example.kotlinweatherapp.R

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.alerts_coming_soon))
            }
        }
        composable(Routes.Settings.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.settings_coming_soon))
            }
        }
    }
}