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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.presentation.navigation.WeatherBottomNavBar
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel, navController: NavHostController) {
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(color = WeatherCardBg, thickness = 1.dp)
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
                onClick = { showDialog = true },
                containerColor = WeatherNavy,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert))
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
                        modifier = Modifier.size(64.dp),
                        tint = WeatherTextSub.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_weather_alerts_set),
                        color = WeatherNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.tap_to_add_alert),
                        color = WeatherTextSub,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    viewModel.addAlert(type, sH, sM, eH, eM, isAlarm)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AlertItemCard(alert: AlertEntity, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    val conditionLabel = when (alert.alertType) {
        "Rain" -> stringResource(R.string.condition_rain)
        "Snow" -> stringResource(R.string.condition_snow)
        "Clear" -> stringResource(R.string.condition_clear)
        "Clouds" -> stringResource(R.string.condition_clouds)
        "Thunderstorm" -> stringResource(R.string.condition_thunderstorm)
        "Drizzle" -> stringResource(R.string.condition_drizzle)
        else -> alert.alertType
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBg)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (alert.isEnabled) WeatherNavy else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (alert.isEnabled) {
                    if (alert.isAlarm) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications
                } else Icons.Outlined.NotificationsOff,
                null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                conditionLabel,
                color = WeatherNavy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${alert.timeDuration} (${
                    if (alert.isAlarm) stringResource(R.string.alarm) else stringResource(
                        R.string.notification
                    )
                })",
                color = WeatherTextSub,
                fontSize = 13.sp
            )
        }
        Switch(
            checked = alert.isEnabled, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = WeatherNavy
            )
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.delete),
                tint = Color(0xFFE57373)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit, onAdd: (
        String,
        Int,
        Int,
        Int,
        Int,
        Boolean
    ) -> Unit
) {
    val weatherConditions = mapOf(
        "Rain" to R.string.condition_rain,
        "Snow" to R.string.condition_snow,
        "Clear" to R.string.condition_clear,
        "Clouds" to R.string.condition_clouds,
        "Thunderstorm" to R.string.condition_thunderstorm,
        "Drizzle" to R.string.condition_drizzle
    )
    
    var expanded by remember { mutableStateOf(false) }
    var selectedKey by remember { mutableStateOf(weatherConditions.keys.first()) }
    
    var isAlarm by remember { mutableStateOf(false) }

    val currentTime = Calendar.getInstance()
    var startHour by remember { mutableIntStateOf(currentTime.get(Calendar.HOUR_OF_DAY)) }
    var startMinute by remember { mutableIntStateOf(currentTime.get(Calendar.MINUTE)) }
    var endHour by remember { mutableIntStateOf((currentTime.get(Calendar.HOUR_OF_DAY) + 1) % 24) }
    var endMinute by remember { mutableIntStateOf(currentTime.get(Calendar.MINUTE)) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_weather_alert),
                    color = WeatherNavy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.alert_type), color = WeatherNavy, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = stringResource(weatherConditions[selectedKey]!!),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            weatherConditions.forEach { (key, resId) ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(resId)) },
                                    onClick = {
                                        selectedKey = key
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.start_time),
                            color = WeatherNavy,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(WeatherCardBg)
                                .clickable { showStartTimePicker = true }
                                .padding(12.dp)
                        ) {
                            Text(
                                String.format("%02d:%02d", startHour, startMinute),
                                color = WeatherNavy
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.end_time),
                            color = WeatherNavy,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(WeatherCardBg)
                                .clickable { showEndTimePicker = true }
                                .padding(12.dp)
                        ) {
                            Text(
                                String.format("%02d:%02d", endHour, endMinute),
                                color = WeatherNavy
                            )
                        }
                    }
                }

                Column {
                    Text(
                        stringResource(R.string.alert_method),
                        color = WeatherNavy,
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isAlarm, onClick = { isAlarm = false })
                        Text(
                            stringResource(R.string.notification),
                            color = WeatherNavy,
                            modifier = Modifier.clickable { isAlarm = false })
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = isAlarm, onClick = { isAlarm = true })
                        Text(
                            stringResource(R.string.alarm),
                            color = WeatherNavy,
                            modifier = Modifier.clickable { isAlarm = true })
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
                    ) { Text(stringResource(R.string.cancel), color = WeatherNavy) }
                    Button(
                        onClick = {
                            onAdd(
                                selectedKey,
                                startHour,
                                startMinute,
                                endHour,
                                endMinute,
                                isAlarm
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WeatherNavy)
                    ) { Text(stringResource(R.string.add), color = Color.White) }
                }
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                startHour = h
                startMinute = m
                showStartTimePicker = false
            },
            initialHour = startHour,
            initialMinute = startMinute
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { h, m ->
                endHour = h
                endMinute = m
                showEndTimePicker = false
            },
            initialHour = endHour,
            initialMinute = endMinute
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val state = rememberTimePickerState(initialHour, initialMinute, true)
    var showKeyboard by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.select_time),
                    color = WeatherNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(16.dp))

                if (showKeyboard) {
                    TimeInput(state = state)
                } else {
                    TimePicker(state = state)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showKeyboard = !showKeyboard }) {
                        Icon(
                            if (showKeyboard) Icons.Outlined.Notifications else Icons.Outlined.NotificationsActive,
                            contentDescription = null
                        )
                    }
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text(
                                stringResource(R.string.cancel),
                                color = WeatherNavy
                            )
                        }
                        TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                            Text(
                                stringResource(R.string.add),
                                color = WeatherNavy
                            )
                        }
                    }
                }
            }
        }
    }
}