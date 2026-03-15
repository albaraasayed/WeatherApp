package com.example.kotlinweatherapp.presentation.features.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.data.local.dao.AlertDao
import com.example.kotlinweatherapp.presentation.features.alerts.background.AlertScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AlertsViewModel(
    private val dao: AlertDao,
    private val scheduler: AlertScheduler
) : ViewModel() {

    val alerts = dao.getAllAlerts()
        .stateIn(
            viewModelScope, SharingStarted
                .WhileSubscribed(5000),
            emptyList()
        )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun addAlert(
        type: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isAlarm: Boolean
    ) {
        viewModelScope.launch {
            val startCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val endCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= startCalendar.timeInMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val timeDuration =
                String.format(
                    "%02d:%02d - %02d:%02d",
                    startHour,
                    startMinute,
                    endHour,
                    endMinute
                )

            val alert = AlertEntity(
                alertType = type,
                timeDuration = timeDuration,
                startTimeInMillis = startCalendar.timeInMillis,
                endTimeInMillis = endCalendar.timeInMillis,
                isAlarm = isAlarm,
                isEnabled = true
            )
            val id = dao.insertAlert(alert)
            scheduler.schedule(alert.copy(id = id.toInt()))

            _eventFlow.emit("Alert set for $timeDuration")
        }
    }

    fun toggleAlert(alert: AlertEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = isEnabled)

            if (isEnabled) {
                val startCal =
                    Calendar.getInstance().apply { timeInMillis = updated.startTimeInMillis }
                val endCal = Calendar.getInstance().apply { timeInMillis = updated.endTimeInMillis }

                if (startCal.timeInMillis <= System.currentTimeMillis()) {
                    val diff = endCal.timeInMillis - startCal.timeInMillis
                    startCal.set(
                        Calendar.DAY_OF_YEAR,
                        Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    )
                    if (startCal.timeInMillis <= System.currentTimeMillis()) {
                        startCal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    endCal.timeInMillis = startCal.timeInMillis + diff
                }

                val finalAlert = updated.copy(
                    startTimeInMillis = startCal.timeInMillis,
                    endTimeInMillis = endCal.timeInMillis
                )
                dao.updateAlert(finalAlert)
                scheduler.schedule(finalAlert)
                _eventFlow.emit("Alert enabled")
            } else {
                dao.updateAlert(updated)
                scheduler.cancel(updated)
                _eventFlow.emit("Alert disabled")
            }
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            scheduler.cancel(alert)
            dao.deleteAlert(alert)
            _eventFlow.emit("Alert deleted")
        }
    }
}