package com.example.kotlinweatherapp.presentation.features.alerts.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.kotlinweatherapp.data.local.AlertEntity

class AlertScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alert: AlertEntity) {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
            putExtra("ALERT_TYPE", alert.alertType)
            putExtra("IS_ALARM", alert.isAlarm)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.startTimeInMillis,
            pendingIntent
        )
    }

    fun cancel(alert: AlertEntity) {
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}