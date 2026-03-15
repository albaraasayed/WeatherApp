package com.example.kotlinweatherapp.presentation.features.favorites.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub


@Composable
fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = WeatherTextSub.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_favorite_locations_saved),
            color = WeatherNavy,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.tap_to_add_location),
            color = WeatherTextSub,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FavoriteItemCard(location: FavoriteLocation, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBg)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WeatherNavy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.cityName,
                color = WeatherNavy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${String.format("%.4f", location.latitude)}, ${
                    String.format(
                        "%.4f",
                        location.longitude
                    )
                }",
                color = WeatherTextSub,
                fontSize = 13.sp
            )
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.delete),
                tint = Color(0xFFE57373)
            )
        }
    }
}