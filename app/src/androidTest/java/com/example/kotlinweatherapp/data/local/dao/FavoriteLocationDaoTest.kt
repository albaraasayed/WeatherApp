package com.example.kotlinweatherapp.data.local.dao
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.kotlinweatherapp.data.local.FavoriteLocation
import com.example.kotlinweatherapp.data.local.WeatherDatabase
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
@SmallTest
@ExperimentalCoroutinesApi
class FavoriteLocationDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: FavoriteLocationDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteLocationDao
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertFavorite_retrievesFavorite() = runTest {
        // Given
        val favorite = FavoriteLocation(cityName = "Cairo", latitude = 30.04, longitude = 31.23)

        // When
        dao.insertFavorite(favorite)

        // Then
        val result = dao.getAllFavorites().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Cairo"))
        assertThat(result[0].latitude, `is`(30.04))
    }

    @Test
    fun deleteFavorite_removesFromDatabase() = runTest {
        // Given
        val favorite = FavoriteLocation(cityName = "Alexandria", latitude = 31.20, longitude = 29.91)
        dao.insertFavorite(favorite)

        // When
        val insertedFavorite = dao.getAllFavorites().first()[0]
        dao.deleteFavorite(insertedFavorite)

        // Then
        val result = dao.getAllFavorites().first()
        assertThat(result.isEmpty(), `is`(true))
    }
}