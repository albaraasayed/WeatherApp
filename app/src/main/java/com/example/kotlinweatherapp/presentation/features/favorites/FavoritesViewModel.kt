package com.example.kotlinweatherapp.presentation.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val dao: FavoriteLocationDao
) : ViewModel() {
    val favorites: StateFlow<List<FavoriteLocation>> = dao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFavorite(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            dao.insertFavorite(
                FavoriteLocation(
                    cityName = cityName,
                    latitude = lat,
                    longitude = lon
                )
            )
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            dao.deleteFavorite(location)
        }
    }
}

class FavoritesViewModelFactory(
    private val dao: FavoriteLocationDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(dao) as T
    }
}