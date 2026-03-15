package com.example.kotlinweatherapp.presentation.features.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.presentation.Dimens
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel,
    navController: NavHostController
) {
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.weather_alerts),
                            color = WeatherNavy,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
                HorizontalDivider(
                    color = WeatherCardBg,
                    thickness = Dimens.spacingTiny.div(4)
                )
            }
        },
        bottomBar = {
            WeatherBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = WeatherNavy,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_alert)
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
            if (alerts.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconExtraLarge),
                        tint = WeatherTextSub.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(Dimens.spacingLarge))
                    Text(
                        text = stringResource(R.string.no_weather_alerts_set),
                        color = WeatherNavy,
                        fontSize = Dimens.fontSubTitle,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(Dimens.spacingSmall))
                    Text(
                        text = stringResource(R.string.tap_to_add_alert),
                        color = WeatherTextSub,
                        fontSize = Dimens.fontBodySmall
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.spacingExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMedium)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        AlertItemCard(
                            alert,
                            onToggle = { viewModel.toggleAlert(alert, it) },
                            onDelete = { viewModel.deleteAlert(alert) })
                    }
                }
            }
        }

        if (showDialog) {
            AddAlertDialog(
                onDismiss = { showDialog = false },
                onAdd = { type, sH, sM, eH, eM, isAlarm ->
                    viewModel.addAlert(
                        type,
                        sH,
                        sM,
                        eH,
                        eM,
                        isAlarm
                    )
                    showDialog = false
                }
            )
        }
    }
}