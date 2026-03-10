package com.example.kotlinweatherapp.presentation.features.favorites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub

@Composable
fun AddLocationDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

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
                // Title
                Text(
                    text = stringResource(R.string.add_favorite_location),
                    color = WeatherNavy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Search Input Field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.location_name),
                        color = WeatherNavy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.location_placeholder),
                                color = WeatherTextSub.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = WeatherCardBg,
                            focusedBorderColor = WeatherNavy,
                            cursorColor = WeatherNavy
                        ),
                        singleLine = true
                    )
                }

                // Tip Box (Placeholder for MapLibre Map!)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WeatherCardBg.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.tip),
                            color = WeatherNavy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.tip_description),
                            color = WeatherNavy.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, WeatherNavy),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WeatherNavy)
                    ) {
                        Text(text = stringResource(R.string.cancel), modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Button(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                onAdd(searchQuery)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WeatherNavy)
                    ) {
                        Text(text = stringResource(R.string.add), modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}