package com.example.kotlinweatherapp.presentation.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.kotlinweatherapp.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinweatherapp.presentation.common.*
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherBackground
import com.example.kotlinweatherapp.ui.theme.WeatherNavy

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            WeatherBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = WeatherBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = WeatherNavy
                    )
                }

                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = WeatherNavy)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.loadWeather(viewModel.currentLat, viewModel.currentLon)
                        }) { Text(stringResource(R.string.retry)) }
                    }
                }

                is HomeUiState.Success -> {
                    WeatherContentDisplay(data = state.data)
                }
            }
        }
    }
}