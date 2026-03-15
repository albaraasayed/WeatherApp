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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import com.example.kotlinweatherapp.presentation.WeatherViewModelFactory
import com.example.kotlinweatherapp.presentation.features.alerts.AlertsViewModel
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

        val factory = WeatherViewModelFactory(this)

        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        val favoritesViewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]
        val alertsViewModel = ViewModelProvider(this, factory)[AlertsViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        val favoriteDetailsViewModel = ViewModelProvider(this, factory)[FavoriteDetailsViewModel::class.java]

        settingsRepo = SettingsRepository(this)
        
        setContent {
            WeatherAppTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current

                // Initialize LocationHelper with a callback to show Snackbars
                locationHelper = remember {
                    LocationHelper(
                        activity = this@MainActivity,
                        onLocationFetched = { lat, lon ->
                            lifecycleScope.launch { settingsRepo.saveLastGpsLocation(lat, lon) }
                            homeViewModel.loadWeather(lat, lon)
                        },
                        onLocationFailed = { homeViewModel.loadWeather(31.2001, 29.9187) },
                        onMessage = { message ->
                            lifecycleScope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        lifecycleScope.launch {
                            snackbarHostState.showSnackbar("Notification permission denied. Weather alerts will not be shown.")
                        }
                    }
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

                // Collect GPS trigger
                LaunchedEffect(Unit) {
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

                val navController = rememberNavController()
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
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
