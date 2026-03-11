package com.example.kotlinweatherapp.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.datastore.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val locationPref: StateFlow<String> = repository.locationPrefFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")
    val tempUnitPref: StateFlow<String> = repository.tempUnitPrefFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")
    val windUnitPref: StateFlow<String> = repository.windUnitPrefFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ms")
    val langPref: StateFlow<String> = repository.langPrefFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun updateLocationPref(value: String) = viewModelScope.launch { repository.saveLocationPref(value) }
    fun updateTempUnitPref(value: String) = viewModelScope.launch { repository.saveTempUnitPref(value) }
    fun updateWindUnitPref(value: String) = viewModelScope.launch { repository.saveWindUnitPref(value) }
    fun updateLangPref(value: String) = viewModelScope.launch { repository.saveLangPref(value) }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }
}