package com.example.kotlinweatherapp.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub

data class NavItem(
    val label: String,
    val iconRes: Int
)

@Composable
fun WeatherBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("Home", R.drawable.ic_nav_home),
        NavItem("Favorites", R.drawable.ic_nav_favorites),
        NavItem("Alerts", R.drawable.ic_nav_alerts),
        NavItem("Settings", R.drawable.ic_nav_settings)
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WeatherNavy,
                    selectedTextColor = WeatherNavy,
                    unselectedIconColor = WeatherTextSub,
                    unselectedTextColor = WeatherTextSub,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}