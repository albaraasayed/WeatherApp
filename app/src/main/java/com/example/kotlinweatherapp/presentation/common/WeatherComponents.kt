package com.example.kotlinweatherapp.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.presentation.Dimens
import com.example.kotlinweatherapp.presentation.features.home.DailyForecastUi
import com.example.kotlinweatherapp.presentation.features.home.HourlyForecastUi
import com.example.kotlinweatherapp.presentation.features.home.WeatherDataUi
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub

@Composable
fun WeatherContentDisplay(
    data: WeatherDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WeatherHeaderSection(city = data.city, date = data.date, time = data.time)
        CurrentWeatherHero(
            temperature = data.temperature,
            unitSymbol = data.unitSymbol,
            condition = data.condition,
            iconUrl = data.iconUrl
        )
        WeatherStatsCard(
            humidity = data.humidity,
            windSpeed = data.windSpeed,
            pressure = data.pressure,
            clouds = data.clouds
        )

        Spacer(modifier = Modifier.height(Dimens.spacingHuge))
        HourlyForecastList(forecasts = data.hourlyForecast)
        Spacer(modifier = Modifier.height(Dimens.spacingHuge))
        DailyForecastList(forecasts = data.dailyForecast)
        Spacer(modifier = Modifier.height(Dimens.spacingLarge))
    }
}

@Composable
fun CurrentWeatherHero(
    temperature: Int,
    unitSymbol: String,
    condition: String,
    iconUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacingHuge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = condition,
            modifier = Modifier.size(Dimens.iconHero)
        )
        Spacer(modifier = Modifier.height(Dimens.spacingLarge))
        Text(
            text = "$temperature$unitSymbol",
            fontSize = Dimens.fontHero,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        Spacer(modifier = Modifier.height(Dimens.spacingTiny))
        Text(text = condition, fontSize = Dimens.fontSubTitle, color = WeatherTextSub)
    }
}

@Composable
fun HourlyForecastList(
    forecasts: List<HourlyForecastUi>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = Dimens.spacingExtraLarge)) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            fontSize = Dimens.fontBodyLarge,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        Spacer(modifier = Modifier.height(Dimens.spacingMedium))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingNormal)) {
            items(forecasts) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.cornerSmall))
                        .background(WeatherCardBg)
                        .padding(horizontal = Dimens.spacingExtraLarge, vertical = Dimens.spacingMedium)
                ) {
                    Text(text = item.time, fontSize = Dimens.fontCaption, color = WeatherTextSub)
                    Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconStandard)
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                    Text(
                        text = "${item.temperature}${item.unitSymbol}",
                        fontSize = Dimens.fontBodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastList(
    forecasts: List<DailyForecastUi>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = Dimens.spacingExtraLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingNormal)
    ) {
        Text(
            text = stringResource(R.string.five_day_forecast),
            fontSize = Dimens.fontBodyLarge,
            fontWeight = FontWeight.Bold,
            color = WeatherNavy
        )
        forecasts.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.cornerMedium))
                    .background(WeatherCardBg)
                    .padding(horizontal = Dimens.spacingLarge, vertical = Dimens.spacingBodyLarge)
            ) {
                AsyncImage(
                    model = item.iconUrl,
                    contentDescription = item.day,
                    modifier = Modifier.size(Dimens.iconLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.spacingBodyLarge))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.day,
                        fontSize = Dimens.fontBody,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                    Text(text = item.date, fontSize = Dimens.fontCaption, color = WeatherTextSub)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.highTemp}${item.unitSymbol}",
                        fontSize = Dimens.fontBody,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherNavy
                    )
                    Text(text = "${item.lowTemp}${item.unitSymbol}", fontSize = Dimens.fontCaption, color = WeatherTextSub)
                }
            }
        }
    }
}

@Composable
fun WeatherStatsCard(
    humidity: Int,
    windSpeed: Double,
    pressure: Int,
    clouds: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingExtraLarge)
            .clip(RoundedCornerShape(Dimens.cornerLarge))
            .background(WeatherCardBg)
            .padding(Dimens.spacingLarge)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLarge)) {
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
fun StatTile(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(Dimens.iconMedium)
        )
        Spacer(modifier = Modifier.width(Dimens.spacingNormal))
        Column {
            Text(text = label, fontSize = Dimens.fontCaption, color = WeatherTextSub)
            Text(
                text = value,
                fontSize = Dimens.fontBody,
                fontWeight = FontWeight.SemiBold,
                color = WeatherNavy
            )
        }
    }
}

@Composable
fun WeatherHeaderSection(
    city: String,
    date: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = Dimens.spacingExtraLarge, vertical = Dimens.spacingLarge)) {
        Text(text = city, fontSize = Dimens.fontHeader, fontWeight = FontWeight.Bold, color = WeatherNavy)
        Spacer(modifier = Modifier.height(Dimens.spacingTiny))
        Text(text = date, fontSize = Dimens.fontBodySmall, color = WeatherTextSub)
        Text(text = time, fontSize = Dimens.fontBodySmall, color = WeatherTextSub)
    }
}