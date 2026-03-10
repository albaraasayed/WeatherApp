package com.example.kotlinweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.kotlinweatherapp.data.local.WeatherDatabase
import com.example.kotlinweatherapp.data.remote.RetrofitClient
import com.example.kotlinweatherapp.data.repository.WeatherRepositoryImpl
import com.example.kotlinweatherapp.presentation.features.favorites.FavoritesViewModel
import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import com.example.kotlinweatherapp.presentation.navigation.NavGraph
import com.example.kotlinweatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = WeatherRepositoryImpl(RetrofitClient.apiService)
        val database = WeatherDatabase.getInstance(applicationContext)
        val favDao = database.favoriteLocationDao

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                val homeViewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
                val favoritesViewModel: FavoritesViewModel = viewModel { FavoritesViewModel(favDao) }

                NavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    favoritesViewModel = favoritesViewModel
                )
            }
        }
    }
}