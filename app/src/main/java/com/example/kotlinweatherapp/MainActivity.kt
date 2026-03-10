package com.example.kotlinweatherapp

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinweatherapp.data.remote.RetrofitClient
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import com.example.kotlinweatherapp.data.repository.WeatherRepositoryImpl
import com.example.kotlinweatherapp.presentation.features.home.HomeScreen
import com.example.kotlinweatherapp.presentation.features.home.HomeViewModel
import com.example.kotlinweatherapp.ui.theme.WeatherAppTheme

class HomeViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = WeatherRepositoryImpl(RetrofitClient.apiService)
        val factory = HomeViewModelFactory(repository)

        val homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setContent {
            WeatherAppTheme {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}