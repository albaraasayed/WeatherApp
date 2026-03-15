package com.example.kotlinweatherapp.presentation.features.favorites.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.repository.WeatherRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {
    private lateinit var dao: FavoriteLocationDao
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        viewModel = FavoritesViewModel(dao, repository)
    }

    @Test
    fun addFavorite_insertsToDao() = runTest {
        // Given
        val cityName = "Cairo"
        val lat = 30.04
        val lon = 31.23

        // When
        viewModel.addFavorite(cityName, lat, lon)

        // Then
        coVerify { dao.insertFavorite(any()) }
    }

    @Test
    fun onSearchQueryChanged_shortQuery_returnsEmpty() = runTest {
        // Given
        val shortQuery = "Ca"

        // When
        viewModel.onSearchQueryChanged(shortQuery)

        // Then
        val result = viewModel.searchResults.value
        assertThat(result.data, `is`(emptyList()))
    }
}