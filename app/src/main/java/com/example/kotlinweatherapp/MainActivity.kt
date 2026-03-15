package com.example.kotlinweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.kotlinweatherapp.data.local.WeatherDatabase
import com.example.kotlinweatherapp.data.local.datasource.WeatherLocalDataSourceImpl
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import com.example.kotlinweatherapp.data.remote.RetrofitClient
import com.example.kotlinweatherapp.data.remote.datasource.WeatherRemoteDataSourceImpl
import com.example.kotlinweatherapp.data.repository.WeatherRepositoryImpl
import com.example.kotlinweatherapp.presentation.features.alerts.AlertsViewModel
import com.example.kotlinweatherapp.presentation.features.alerts.background.AlertScheduler
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoriteDetailsViewModel
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoritesViewModel

import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import com.example.kotlinweatherapp.presentation.features.settings.SettingsViewModel
import com.example.kotlinweatherapp.presentation.navigation.NavGraph
import com.example.kotlinweatherapp.ui.theme.WeatherAppTheme
import com.example.kotlinweatherapp.utils.LocationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var settingsRepo: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = WeatherDatabase.getInstance(applicationContext)
        val cacheDao = database.weatherCacheDao
        val favDao = database.favoriteLocationDao
        val alertDao = database.alertDao

        val localDataSource = WeatherLocalDataSourceImpl(cacheDao, favDao, alertDao)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitClient.apiService)

        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)

        val alertScheduler = AlertScheduler(applicationContext)
        settingsRepo = SettingsRepository(applicationContext)

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

        val alertsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AlertsViewModel(alertDao, alertScheduler) as T
        })[AlertsViewModel::class.java]

        val settingsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SettingsViewModel(settingsRepo) as T
        })[SettingsViewModel::class.java]

        val favoriteDetailsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FavoriteDetailsViewModel(repository, settingsRepo) as T
        })[FavoriteDetailsViewModel::class.java]

        locationHelper = LocationHelper(
            activity = this,
            onLocationFetched = { lat, lon ->
                lifecycleScope.launch { settingsRepo.saveLastGpsLocation(lat, lon) }
                homeViewModel.loadWeather(lat, lon)
            },
            onLocationFailed = { homeViewModel.loadWeather(31.2001, 29.9187) }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepo.locationPrefFlow.collect { pref ->
                    if (pref == "gps") {
                        locationHelper.checkAndRequestLocation()
                    } else {
                        val lat = settingsRepo.homeLatFlow.first()
                        val lon = settingsRepo.homeLonFlow.first()
                        if (lat != 0.0 && lon != 0.0) {
                            homeViewModel.loadWeather(lat, lon)
                        }
                    }
                }
            }
        }

        setContent {
            WeatherAppTheme {
                val context = LocalContext.current
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Handle permission result if needed
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    favoritesViewModel = favoritesViewModel,
                    alertsViewModel = alertsViewModel,
                    settingsViewModel = settingsViewModel,
                    favoriteDetailsViewModel = favoriteDetailsViewModel
                )
            }
        }
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