package com.example.kotlinweatherapp.presentation.features.favorites.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.presentation.common.*
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoriteDetailsViewModel
import com.example.kotlinweatherapp.presentation.features.home.HomeUiState
import com.example.kotlinweatherapp.ui.theme.WeatherBackground
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    viewModel: FavoriteDetailsViewModel,
    lat: Double,
    lon: Double,
    cityName: String,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lat, lon) {
        viewModel.loadWeather(lat, lon)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = cityName, color = WeatherNavy, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WeatherNavy)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(color = WeatherCardBg, thickness = 1.dp)
            }
        },
        containerColor = WeatherBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = WeatherNavy)
                }

                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = WeatherNavy)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadWeather(lat, lon) }) { Text(stringResource(R.string.retry)) }
                    }
                }

                is HomeUiState.Success -> {
                    WeatherContentDisplay(data = state.data)
                }
            }
        }
    }
}