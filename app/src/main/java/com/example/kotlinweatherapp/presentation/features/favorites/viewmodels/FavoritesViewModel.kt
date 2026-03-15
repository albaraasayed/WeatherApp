package com.example.kotlinweatherapp.presentation.features.favorites.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.remote.dto.GeocodingResponse
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import com.example.kotlinweatherapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel(
    private val dao: FavoriteLocationDao,
    private val repository: WeatherRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteLocation>> = dao.getAllFavorites()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults: StateFlow<Resource<List<GeocodingResponse>>> = searchQuery
        .debounce(500L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length < 3 || query.startsWith("Map Pin:")) {
                flowOf(Resource.Success(emptyList()))
            } else {
                repository.searchCity(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Success(emptyList())
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun addFavorite(
        cityName: String,
        lat: Double,
        lon: Double
    ) {
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

    fun addFavoriteFromMap(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch {
            repository.reverseGeocode(lat, lon).collect { result ->
                if (result is Resource.Success) {
                    addFavorite(result.data ?: "Unknown Location", lat, lon)
                } else if (result is Resource.Error) {
                    addFavorite("Unknown Location", lat, lon)
                }
            }
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch { dao.deleteFavorite(location) }
    }
}