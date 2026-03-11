package com.example.kotlinweatherapp.presentation.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val dao: FavoriteLocationDao,
    private val repository: WeatherRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteLocation>> = dao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults =
        MutableStateFlow<Resource<List<GeocodingResponse>>>(Resource.Success(emptyList()))
    val searchResults = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        // 🌟 FIX: Ignore short queries AND Map Pins so we don't spam the API!
        if (query.length < 3 || query.startsWith("Map Pin:")) {
            _searchResults.value = Resource.Success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            repository.searchCity(query).collect { result ->
                _searchResults.value = result
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = Resource.Success(emptyList())
    }

    fun addFavorite(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            dao.insertFavorite(
                FavoriteLocation(
                    cityName = cityName,
                    latitude = lat,
                    longitude = lon
                )
            )
            clearSearch()
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch { dao.deleteFavorite(location) }
    }
}