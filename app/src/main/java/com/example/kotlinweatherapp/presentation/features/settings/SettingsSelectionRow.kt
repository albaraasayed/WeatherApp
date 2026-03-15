package com.example.kotlinweatherapp.presentation.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinweatherapp.ui.theme.WeatherNavy

@Composable
fun SettingsSelectionRow(
    title: String,
    options: List<Pair<String, String>>,
    selectedKey: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = WeatherNavy,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (key, displayText) ->
                val isSelected = key == selectedKey

                Button(
                    onClick = { onSelect(key) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) WeatherNavy else Color.White,
                        contentColor = if (isSelected) Color.White else WeatherNavy
                    ),
                    border = if (isSelected) null else BorderStroke(1.dp, WeatherNavy),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = displayText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}