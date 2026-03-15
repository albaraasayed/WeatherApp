package com.example.kotlinweatherapp.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class WeatherViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val database = WeatherDatabase.getInstance(context.applicationContext)
    private val settingsRepository = SettingsRepository(context.applicationContext)
    private val alertScheduler = AlertScheduler(context.applicationContext)

    private val localDataSource = WeatherLocalDataSourceImpl(
        cacheDao = database.weatherCacheDao,
        favoriteDao = database.favoriteLocationDao,
        alertDao = database.alertDao
    )

    private val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitClient.apiService)

    private val repository = WeatherRepositoryImpl(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(database.favoriteLocationDao, repository) as T
            }
            modelClass.isAssignableFrom(AlertsViewModel::class.java) -> {
                AlertsViewModel(database.alertDao, alertScheduler) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteDetailsViewModel::class.java) -> {
                FavoriteDetailsViewModel(repository, settingsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
