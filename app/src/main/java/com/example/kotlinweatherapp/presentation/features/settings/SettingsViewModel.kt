package com.example.kotlinweatherapp.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val showMapDialog = MutableStateFlow(false)

    val locationPref: StateFlow<String> = repository.locationPrefFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val tempUnitPref: StateFlow<String> = repository.tempUnitPrefFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val windUnitPref: StateFlow<String> = repository.windUnitPrefFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ms")

    val langPref: StateFlow<String> = repository.langPrefFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun updateLocationPref(pref: String) = viewModelScope.launch { repository.saveLocationPref(pref) }
    fun updateTempUnitPref(pref: String) = viewModelScope.launch { repository.saveTempUnitPref(pref) }
    fun updateWindUnitPref(pref: String) = viewModelScope.launch { repository.saveWindUnitPref(pref) }
    fun updateLangPref(pref: String) = viewModelScope.launch { repository.saveLangPref(pref) }

    fun saveHomeLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.saveHomeLocation(lat, lon)
            repository.saveLocationPref("map")
        }
    }
}