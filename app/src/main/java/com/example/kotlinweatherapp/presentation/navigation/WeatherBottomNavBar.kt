package com.example.kotlinweatherapp.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub

data class NavItem(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val route: String
)

@Composable
fun WeatherBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            R.string.nav_home,
            R.drawable.ic_nav_home,
            Routes.Home.route
        ),
        NavItem(
            R.string.nav_favorites,
            R.drawable.ic_nav_favorites,
            Routes.Favorites.route
        ),
        NavItem(
            R.string.nav_alerts,
            R.drawable.ic_nav_alerts,
            Routes.Alerts.route
        ),
        NavItem(
            R.string.nav_settings,
            R.drawable.ic_nav_settings,
            Routes.Settings.route
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) onNavigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = stringResource(id = item.labelRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = stringResource(id = item.labelRes))
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