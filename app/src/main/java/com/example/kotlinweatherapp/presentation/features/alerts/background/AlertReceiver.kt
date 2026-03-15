package com.example.kotlinweatherapp.presentation.features.alerts.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertType = intent.getStringExtra("ALERT_TYPE") ?: "Weather Update"
        val isAlarm = intent.getBooleanExtra("IS_ALARM", false)

        val workData = workDataOf(
            "ALERT_TYPE" to alertType,
            "IS_ALARM" to isAlarm
        )
        val workRequest = OneTimeWorkRequestBuilder<WeatherCheckWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}