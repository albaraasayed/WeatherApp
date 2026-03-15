package com.example.kotlinweatherapp.presentation.features.alerts.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kotlinweatherapp.R
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import com.example.kotlinweatherapp.data.remote.RetrofitClient
import kotlinx.coroutines.flow.first

class WeatherCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val alertType = inputData.getString("ALERT_TYPE") ?: return Result.success()
        val isAlarm = inputData.getBoolean("IS_ALARM", false)

        val settingsRepo = SettingsRepository(applicationContext)
        val locationPref = settingsRepo.locationPrefFlow.first()

        val (lat, lon) = if (locationPref == "gps") {
            Pair(settingsRepo.lastGpsLatFlow.first(), settingsRepo.lastGpsLonFlow.first())
        } else {
            Pair(settingsRepo.homeLatFlow.first(), settingsRepo.homeLonFlow.first())
        }

        if (lat == 0.0 && lon == 0.0) {
            showNotification(
                title = "Alert Postponed",
                message = "Location missing! Open the app once to sync your GPS or set a home location in Settings.",
                isAlarm = isAlarm
            )
            return Result.success()
        }

        try {
            val response = RetrofitClient.apiService.getWeatherForecast(
                lat = lat,
                lon = lon,
                apiKey = com.example.kotlinweatherapp.utils.Constants.API_KEY
            )

            if (response.isSuccessful) {
                val currentCondition = response.body()?.list?.get(0)?.weather?.get(0)?.main
                
                if (currentCondition?.equals(alertType, ignoreCase = true) == true) {
                    val message = "Condition matched: $currentCondition"
                    showNotification(alertType, message, isAlarm)
                }
            }
        } catch (e: Exception) {
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String, isAlarm: Boolean) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (isAlarm) "weather_alarm_channel" else "weather_alerts_channel"
        val channelName = if (isAlarm) "Weather Alarms" else "Weather Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance =
                if (isAlarm) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                if (isAlarm) {
                    val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val attributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    setSound(soundUri, attributes)
                    enableVibration(true)
                }
            }
            manager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_nav_alerts)
            .setPriority(
                if (isAlarm) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)

        if (isAlarm) {
            val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            notificationBuilder.setSound(soundUri)
            notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)
        }
        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}