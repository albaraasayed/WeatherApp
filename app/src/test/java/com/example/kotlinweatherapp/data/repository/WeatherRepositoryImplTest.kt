package com.example.kotlinweatherapp.data.repository

import com.example.kotlinweatherapp.data.local.CachedWeather
import com.example.kotlinweatherapp.data.local.datasource.WeatherLocalDataSource
import com.example.kotlinweatherapp.data.remote.datasource.WeatherRemoteDataSource
import com.example.kotlinweatherapp.data.remote.dto.WeatherResponse
import com.example.kotlinweatherapp.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class WeatherRepositoryImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSource
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = mockk(relaxed = true)
        localDataSource = mockk(relaxed = true)
        repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun getForecast_cachedDataAvailable_returnsCachedData() = runTest {
        // Given
        val cachedWeather = CachedWeather(id = "30.04_31.23", weatherData = mockk(relaxed = true))
        coEvery { localDataSource.getCachedWeather(any()) } returns cachedWeather

        // When
        val result = repository.getForecast(30.04, 31.23, "metric", "en").first()

        // Then
        assertThat(result, instanceOf(Resource.Success::class.java))
        assertThat(result.data, `is`(cachedWeather.weatherData))
    }

    @Test
    fun getForecast_networkError_returnsError() = runTest {
        // Given
        coEvery { localDataSource.getCachedWeather(any()) } returns null
        coEvery { remoteDataSource.getForecast(any(), any(), any(), any(), any()) } throws IOException()

        // When
        val result = repository.getForecast(30.04, 31.23, "metric", "en").drop(1).first()

        // Then
        assertThat(result, instanceOf(Resource.Error::class.java))
    }
}