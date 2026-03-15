package com.example.kotlinweatherapp.data.local.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.kotlinweatherapp.data.local.AlertEntity
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.WeatherDatabase
import com.example.kotlinweatherapp.data.local.dao.AlertDao
import com.example.kotlinweatherapp.data.local.dao.FavoriteLocationDao
import com.example.kotlinweatherapp.data.local.dao.WeatherCacheDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class WeatherLocalDataSourceImplTest {

    private lateinit var database: WeatherDatabase
    private lateinit var cacheDao: WeatherCacheDao
    private lateinit var favoriteDao: FavoriteLocationDao
    private lateinit var alertDao: AlertDao
    private lateinit var localDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        cacheDao = database.weatherCacheDao
        favoriteDao = database.favoriteLocationDao
        alertDao = database.alertDao

        localDataSource = WeatherLocalDataSourceImpl(cacheDao, favoriteDao, alertDao)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertFavorite_retrievesFavorite() = runTest {
        // Given
        val favorite = FavoriteLocation(cityName = "Alexandria", latitude = 31.2, longitude = 29.9)

        // When
        localDataSource.insertFavorite(favorite)

        // Then
        val result = localDataSource.getAllFavorites().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Alexandria"))
        assertThat(result[0].latitude, `is`(31.2))
    }

    @Test
    fun insertAlert_retrievesAlert() = runTest {
        // Given
        val alert = AlertEntity(
            alertType = "Rain",
            timeDuration = "1 hour",
            startTimeInMillis = 100L,
            endTimeInMillis = 200L
        )

        // When
        localDataSource.insertAlert(alert)

        // Then
        val result = localDataSource.getAllAlerts().first()
        assertThat(result.isNotEmpty(), `is`(true))
    }
}