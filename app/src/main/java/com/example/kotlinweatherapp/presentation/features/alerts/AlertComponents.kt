package com.example.kotlinweatherapp.presentation.features.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.presentation.Dimens
import com.example.kotlinweatherapp.ui.theme.WeatherCardBg
import com.example.kotlinweatherapp.ui.theme.WeatherNavy
import com.example.kotlinweatherapp.ui.theme.WeatherTextSub
import java.util.Calendar

@Composable
fun AlertItemCard(
    alert: AlertEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
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
            .clip(RoundedCornerShape(Dimens.cornerLarge))
            .background(WeatherCardBg)
            .padding(Dimens.spacingLarge)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconStandard)
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
                modifier = Modifier.size(Dimens.iconSmall)
            )
        }
        Spacer(Modifier.width(Dimens.spacingLarge))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                conditionLabel,
                color = WeatherNavy,
                fontSize = Dimens.fontBodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${alert.timeDuration} (${
                    if (alert.isAlarm) stringResource(R.string.alarm) else stringResource(
                        R.string.notification
                    )
                })",
                color = WeatherTextSub,
                fontSize = Dimens.fontCaption
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
            shape = RoundedCornerShape(Dimens.cornerExtraLarge),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.spacingHuge)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLarge)
            ) {
                Text(
                    text = stringResource(R.string.add_weather_alert),
                    color = WeatherNavy,
                    fontSize = Dimens.fontTitle,
                    fontWeight = FontWeight.Bold
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.alert_type),
                        color = WeatherNavy,
                        fontSize = Dimens.fontBodySmall
                    )
                    Spacer(Modifier.height(Dimens.spacingTiny))

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
                            shape = RoundedCornerShape(Dimens.cornerSmall),
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
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.start_time),
                            color = WeatherNavy,
                            fontSize = Dimens.fontBodySmall
                        )
                        Spacer(Modifier.height(Dimens.spacingTiny))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.cornerSmall))
                                .background(WeatherCardBg)
                                .clickable { showStartTimePicker = true }
                                .padding(Dimens.spacingMedium)
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
                            fontSize = Dimens.fontBodySmall
                        )
                        Spacer(Modifier.height(Dimens.spacingTiny))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.cornerSmall))
                                .background(WeatherCardBg)
                                .clickable { showEndTimePicker = true }
                                .padding(Dimens.spacingMedium)
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
                        fontSize = Dimens.fontBodySmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isAlarm, onClick = { isAlarm = false })
                        Text(
                            stringResource(R.string.notification),
                            color = WeatherNavy,
                            modifier = Modifier.clickable { isAlarm = false })
                        Spacer(Modifier.width(Dimens.spacingLarge))
                        RadioButton(selected = isAlarm, onClick = { isAlarm = true })
                        Text(
                            stringResource(R.string.alarm),
                            color = WeatherNavy,
                            modifier = Modifier.clickable { isAlarm = true })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMedium)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(Dimens.cornerSmall),
                        border = BorderStroke(Dimens.spacingTiny.div(4), WeatherNavy)
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
                        shape = RoundedCornerShape(Dimens.cornerSmall),
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
        Surface(shape = RoundedCornerShape(Dimens.cornerExtraLarge), color = Color.White) {
            Column(
                modifier = Modifier.padding(Dimens.spacingHuge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.select_time),
                    color = WeatherNavy,
                    fontSize = Dimens.fontSubTitle,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(Dimens.spacingLarge))

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