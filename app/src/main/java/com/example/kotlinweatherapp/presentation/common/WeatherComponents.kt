package com.example.kotlinweatherapp.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.presentation.features.home.DailyForecastUi
import com.example.kotlinweatherapp.presentation.features.home.HourlyForecastUi
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub

@Composable
fun CurrentWeatherHero(
    temperature: Int,
    condition: String,
    iconUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = condition,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${temperature}°C",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = condition, fontSize = 18.sp, color = WeatherTextSub)
    }
}

@Composable
fun HourlyForecastList(forecasts: List<HourlyForecastUi>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(forecasts) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WeatherCardBg)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(text = item.time, fontSize = 13.sp, color = WeatherTextSub)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${item.temperature}°C",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastList(forecasts: List<DailyForecastUi>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.five_day_forecast),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        forecasts.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WeatherCardBg)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                AsyncImage(
                    model = item.iconUrl,
                    contentDescription = item.day,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.day,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                    Text(text = item.date, fontSize = 13.sp, color = WeatherTextSub)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.highTemp}°C",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                    Text(text = "${item.lowTemp}°C", fontSize = 13.sp, color = WeatherTextSub)
                }
            }
        }
    }
}

@Composable
fun WeatherStatsCard(
    humidity: Int, windSpeed: Double, pressure: Int, clouds: Int, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBg)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatTile(
                    iconRes = R.drawable.ic_humidity,
                    label = stringResource(R.string.humidity),
                    value = "${humidity}%",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    iconRes = R.drawable.ic_wind,
                    label = stringResource(R.string.wind_speed),
                    value = "${windSpeed} m/s",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatTile(
                    iconRes = R.drawable.ic_pressure,
                    label = stringResource(R.string.pressure),
                    value = "${pressure} hPa",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    iconRes = R.drawable.ic_clouds,
                    label = stringResource(R.string.clouds),
                    value = "${clouds}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatTile(iconRes: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 13.sp, color = WeatherTextSub)
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = WeatherNavy
            )
        }
    }
}

@Composable
fun WeatherHeaderSection(city: String, date: String, time: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(text = city, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WeatherNavy)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = date, fontSize = 14.sp, color = WeatherTextSub)
        Text(text = time, fontSize = 14.sp, color = WeatherTextSub)
    }
}