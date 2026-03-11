package com.example.kotlinweatherapp.presentation.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Observe Settings from DataStore
    val locationPref by viewModel.locationPref.collectAsStateWithLifecycle()
    val tempUnitPref by viewModel.tempUnitPref.collectAsStateWithLifecycle()
    val windUnitPref by viewModel.windUnitPref.collectAsStateWithLifecycle()
    val langPref by viewModel.langPref.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            color = WeatherNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(color = WeatherCardBg, thickness = 1.dp)
            }
        },
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
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Location Settings
            SettingsSelectionRow(
                title = stringResource(R.string.location_settings),
                options = listOf(
                    "gps" to stringResource(R.string.gps),
                    "map" to stringResource(R.string.map)
                ),
                selectedKey = locationPref,
                onSelect = { viewModel.updateLocationPref(it) }
            )

            // 2. Temperature Unit
            SettingsSelectionRow(
                title = stringResource(R.string.temperature_unit),
                options = listOf(
                    "metric" to stringResource(R.string.celsius),
                    "imperial" to stringResource(R.string.fahrenheit),
                    "standard" to stringResource(R.string.kelvin)
                ),
                selectedKey = tempUnitPref,
                onSelect = { viewModel.updateTempUnitPref(it) }
            )

            // 3. Wind Speed Unit
            SettingsSelectionRow(
                title = stringResource(R.string.wind_speed_unit),
                options = listOf(
                    "ms" to stringResource(R.string.m_s),
                    "mph" to stringResource(R.string.mph)
                ),
                selectedKey = windUnitPref,
                onSelect = { viewModel.updateWindUnitPref(it) }
            )

            // 4. Language
            SettingsSelectionRow(
                title = stringResource(R.string.language),
                options = listOf(
                    "en" to stringResource(R.string.english),
                    "ar" to stringResource(R.string.arabic)
                ),
                selectedKey = langPref,
                onSelect = { viewModel.updateLangPref(it) }
            )
        }
    }
}
