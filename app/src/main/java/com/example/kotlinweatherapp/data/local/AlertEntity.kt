package com.example.kotlinweatherapp.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alertType: String,
    val timeDuration: String,
    val startTimeInMillis: Long,
    val endTimeInMillis: Long,
    val isAlarm: Boolean = false,
    val isEnabled: Boolean = true
)