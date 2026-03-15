package com.example.kotlinweatherapp.presentation.features.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    val locationPref by viewModel.locationPref.collectAsStateWithLifecycle()
    val tempUnitPref by viewModel.tempUnitPref.collectAsStateWithLifecycle()
    val windUnitPref by viewModel.windUnitPref.collectAsStateWithLifecycle()
    val langPref by viewModel.langPref.collectAsStateWithLifecycle()

    val showMapDialog by viewModel.showMapDialog.collectAsStateWithLifecycle()

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
            SettingsSelectionRow(
                title = stringResource(R.string.location_settings),
                options = listOf(
                    "gps" to stringResource(R.string.gps),
                    "map" to stringResource(R.string.map)
                ),
                selectedKey = locationPref,
                onSelect = { selected ->
                    if (selected == "map") {
                        viewModel.showMapDialog.value = true
                    } else {
                        viewModel.updateLocationPref("gps")
                    }
                }
            )

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

            SettingsSelectionRow(
                title = stringResource(R.string.wind_speed_unit),
                options = listOf(
                    "ms" to stringResource(R.string.m_s),
                    "mph" to stringResource(R.string.mph)
                ),
                selectedKey = windUnitPref,
                onSelect = { viewModel.updateWindUnitPref(it) }
            )

            SettingsSelectionRow(
                title = stringResource(R.string.language),
                options = listOf(
                    "en" to stringResource(R.string.english),
                    "ar" to stringResource(R.string.arabic)
                ),
                selectedKey = langPref,
                onSelect = { selectedLang ->
                    viewModel.updateLangPref(selectedLang)
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(
                            selectedLang
                        )
                    )
                }
            )
        }

        if (showMapDialog) {
            var selectedLat by remember { mutableStateOf<Double?>(null) }
            var selectedLon by remember { mutableStateOf<Double?>(null) }

            Dialog(onDismissRequest = { viewModel.showMapDialog.value = false }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.select_home_location),
                            color = WeatherNavy,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            com.example.kotlinweatherapp.presentation.common.MapLibreMapView(
                                modifier = Modifier.fillMaxSize(),
                                onLocationSelected = { lat, lon ->
                                    selectedLat = lat
                                    selectedLon = lon
                                }
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedLat != null && selectedLon != null) {
                                    viewModel.saveHomeLocation(selectedLat!!, selectedLon!!)
                                    viewModel.showMapDialog.value = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WeatherNavy),
                            enabled = selectedLat != null
                        ) { Text("Set as Home", color = Color.White) }
                    }
                }
            }
        }
    }
}