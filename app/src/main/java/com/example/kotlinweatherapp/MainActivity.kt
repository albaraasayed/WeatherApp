package com.example.kotlinweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.kotlinweatherapp.data.local.WeatherDatabase
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import com.example.kotlinweatherapp.data.remote.RetrofitClient
import com.example.kotlinweatherapp.data.repository.WeatherRepositoryImpl
import com.example.kotlinweatherapp.presentation.features.favorites.FavoritesViewModel
import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import com.example.kotlinweatherapp.presentation.features.settings.SettingsViewModel
import com.example.kotlinweatherapp.presentation.navigation.NavGraph
import com.example.kotlinweatherapp.ui.theme.WeatherAppTheme
import com.example.kotlinweatherapp.utils.LocationHelper

class MainActivity : ComponentActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = WeatherRepositoryImpl(RetrofitClient.apiService)
        val database = WeatherDatabase.getInstance(applicationContext)
        val favDao = database.favoriteLocationDao
        val settingsRepo = SettingsRepository(applicationContext)

        homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(repository, settingsRepo) as T
        })[HomeViewModel::class.java]

        val favoritesViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FavoritesViewModel(favDao, repository) as T
        })[FavoritesViewModel::class.java]

        val settingsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SettingsViewModel(settingsRepo) as T
        })[SettingsViewModel::class.java]

        locationHelper = LocationHelper(
            activity = this,
            onLocationFetched = { lat, lon ->
                homeViewModel.loadWeather(lat, lon)
            },
            onLocationFailed = {
                homeViewModel.loadWeather(31.2001, 29.9187)
            }
        )

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    favoritesViewModel = favoritesViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        locationHelper.checkAndRequestLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.handlePermissionsResult(requestCode, grantResults)
    }
}