package com.example.kotlinweatherapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val LOCATION_PREF = stringPreferencesKey("location_pref")
        val TEMP_UNIT_PREF = stringPreferencesKey("temp_unit_pref")
        val WIND_UNIT_PREF = stringPreferencesKey("wind_unit_pref")
        val LANG_PREF = stringPreferencesKey("lang_pref")
        val HOME_LAT = doublePreferencesKey("home_lat")
        val HOME_LON = doublePreferencesKey("home_lon")
        val LAST_GPS_LAT = doublePreferencesKey("last_gps_lat")
        val LAST_GPS_LON = doublePreferencesKey("last_gps_lon")
    }

    val locationPrefFlow: Flow<String> = context.dataStore.data.map { it[LOCATION_PREF] ?: "gps" }
    val tempUnitPrefFlow: Flow<String> =
        context.dataStore.data.map { it[TEMP_UNIT_PREF] ?: "metric" }
    val windUnitPrefFlow: Flow<String> = context.dataStore.data.map { it[WIND_UNIT_PREF] ?: "ms" }
    val langPrefFlow: Flow<String> = context.dataStore.data.map { it[LANG_PREF] ?: "en" }

    val homeLatFlow: Flow<Double> = context.dataStore.data.map { it[HOME_LAT] ?: 0.0 }
    val homeLonFlow: Flow<Double> = context.dataStore.data.map { it[HOME_LON] ?: 0.0 }

    val lastGpsLatFlow: Flow<Double> = context.dataStore.data.map { it[LAST_GPS_LAT] ?: 0.0 }
    val lastGpsLonFlow: Flow<Double> = context.dataStore.data.map { it[LAST_GPS_LON] ?: 0.0 }

    suspend fun saveLocationPref(value: String) =
        context.dataStore.edit { it[LOCATION_PREF] = value }

    suspend fun saveTempUnitPref(value: String) =
        context.dataStore.edit { it[TEMP_UNIT_PREF] = value }

    suspend fun saveWindUnitPref(value: String) =
        context.dataStore.edit { it[WIND_UNIT_PREF] = value }

    suspend fun saveLangPref(value: String) = context.dataStore.edit { it[LANG_PREF] = value }

    suspend fun saveHomeLocation(lat: Double, lon: Double) = context.dataStore.edit {
        it[HOME_LAT] = lat
        it[HOME_LON] = lon
    }

    suspend fun saveLastGpsLocation(lat: Double, lon: Double) = context.dataStore.edit {
        it[LAST_GPS_LAT] = lat
        it[LAST_GPS_LON] = lon
    }
}