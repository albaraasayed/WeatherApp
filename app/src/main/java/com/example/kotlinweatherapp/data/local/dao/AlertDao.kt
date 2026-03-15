package com.example.kotlinweatherapp.data.local.dao

import androidx.room.*
import com.example.kotlinweatherapp.data.local.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM weather_alerts ORDER BY startTimeInMillis ASC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Update
    suspend fun updateAlert(alert: AlertEntity)
}