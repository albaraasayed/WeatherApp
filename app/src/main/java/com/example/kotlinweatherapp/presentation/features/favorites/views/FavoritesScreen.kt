package com.example.kotlinweatherapp.presentation.features.favorites.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import androidx.compose.ui.res.stringResource
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.presentation.Dimens
import com.example.kotlinweatherapp.presentation.features.favorites.viewmodels.FavoritesViewModel
import com.example.kotlinweatherapp.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel, navController: NavHostController) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.favorite_locations),
                            color = WeatherNavy,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(color = WeatherCardBg, thickness = Dimens.spacingTiny.div(4)) // 1dp
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = WeatherNavy,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_location)
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (favorites.isEmpty()) {
                EmptyFavoritesState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.spacingExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMedium)
                ) {
                    items(favorites) { location ->
                        FavoriteItemCard(
                            location = location,
                            onClick = {
                                navController.navigate(
                                    Routes.FavoriteDetails.createRoute(
                                        location.latitude,
                                        location.longitude,
                                        location.cityName
                                    )
                                )
                            },
                            onDeleteClick = { viewModel.removeFavorite(location) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddLocationDialog(
                searchQuery = searchQuery,
                searchResults = searchResults,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                onDismiss = {
                    viewModel.clearSearch()
                    showAddDialog = false
                },
                onLocationSelected = { name, lat, lon ->
                    viewModel.addFavorite(name, lat, lon)
                    showAddDialog = false
                },
                onMapLocationSelected = { lat, lon ->
                    viewModel.addFavoriteFromMap(lat, lon)
                    showAddDialog = false
                }
            )
        }
    }
}