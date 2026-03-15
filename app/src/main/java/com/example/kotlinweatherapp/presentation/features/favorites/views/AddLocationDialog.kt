package com.example.kotlinweatherapp.presentation.features.favorites.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.presentation.common.MapLibreMapView
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub
import com.example.kotlinweatherapp.utils.Resource

@Composable
fun AddLocationDialog(
    searchQuery: String,
    searchResults: Resource<List<GeocodingResponse>>,
    onQueryChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onLocationSelected: (name: String, lat: Double, lon: Double) -> Unit,
    onMapLocationSelected: (lat: Double, lon: Double) -> Unit
) {
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLon by remember { mutableStateOf<Double?>(null) }
    
    Dialog(onDismissRequest = onDismiss) {
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
                    text = stringResource(R.string.add_favorite_location),
                    color = WeatherNavy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        onQueryChanged(it)
                        selectedLat = null
                        selectedLon = null
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.location_placeholder),
                            color = WeatherTextSub.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (searchQuery.isBlank() || selectedLat != null) {
                        MapLibreMapView(
                            modifier = Modifier.fillMaxSize(),
                            onLocationSelected = { lat, lon ->
                                selectedLat = lat
                                selectedLon = lon
                                onQueryChanged("Map Pin: ${String.format("%.2f", lat)}, ${String.format("%.2f", lon)}")
                            }
                        )
                    } else {
                        when (searchResults) {
                            is Resource.Loading -> CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center), color = WeatherNavy
                            )

                            is Resource.Error -> Text(
                                text = searchResults.message ?: "Unknown Error",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            is Resource.Success -> {
                                val list = searchResults.data ?: emptyList()
                                if (list.isEmpty()) {
                                    val emptyMessage = if (searchQuery.length < 3) {
                                        "Type at least 3 letters to search..."
                                    } else {
                                        "No cities found for '$searchQuery'"
                                    }

                                    Text(
                                        text = emptyMessage,
                                        color = WeatherTextSub,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(list) { city ->
                                            TextButton(onClick = {
                                                onLocationSelected(city.name, city.lat, city.lon)
                                            }, modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    "${city.name}, ${city.country}",
                                                    color = WeatherNavy,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, WeatherNavy)
                    ) {
                        Text(text = stringResource(R.string.cancel), color = WeatherNavy)
                    }
                    Button(
                        onClick = {
                            val lat = selectedLat
                            val lon = selectedLon
                            if (lat != null && lon != null) {
                                onMapLocationSelected(lat, lon)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WeatherNavy),
                        enabled = selectedLat != null
                    ) { Text(text = stringResource(R.string.add), color = Color.White) }
                }
            }
        }
    }
}