package com.weathersnap.data.repository

import android.content.Context
import com.google.gson.Gson
import com.weathersnap.data.local.dao.ReportDao
import com.weathersnap.data.remote.api.GeocodingApi
import com.weathersnap.data.remote.api.WeatherApi
import com.weathersnap.data.remote.model.CurrentWeatherDto
import com.weathersnap.data.remote.model.GeocodingResponseDto
import com.weathersnap.data.remote.model.GeocodingResultDto
import com.weathersnap.data.remote.model.WeatherResponseDto
import com.weathersnap.domain.model.City
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepositoryTest {

    private val geocodingApi = mockk<GeocodingApi>()
    private val weatherApi = mockk<WeatherApi>()
    private val reportDao = mockk<ReportDao>(relaxed = true)
    private val gson = Gson()
    private val context = mockk<Context>(relaxed = true)

    private val repository = WeatherRepositoryImpl(
        geocodingApi = geocodingApi,
        weatherApi = weatherApi,
        reportDao = reportDao,
        gson = gson,
        context = context,
    )

    @Test
    fun `searchCities returns Success when results exist`() = runTest {
        coEvery { geocodingApi.search(query = "lon", any(), any(), any()) } returns GeocodingResponseDto(
            results = listOf(
                GeocodingResultDto(name = "London", country = "UK", latitude = 51.5, longitude = -0.1),
            ),
        )

        val result = repository.searchCities("lon")
        assertTrue(result is RepoResult.Success)
        val cities = (result as RepoResult.Success).data
        assertEquals(1, cities.size)
        assertEquals("London", cities.first().name)
    }

    @Test
    fun `searchCities returns Empty when results list empty`() = runTest {
        coEvery { geocodingApi.search(query = "zzz", any(), any(), any()) } returns GeocodingResponseDto(results = emptyList())
        val result = repository.searchCities("zzz")
        assertTrue(result is RepoResult.Empty)
    }

    @Test
    fun `searchCities returns Error on IOException`() = runTest {
        coEvery { geocodingApi.search(query = "lon", any(), any(), any()) } throws IOException("no internet")
        val result = repository.searchCities("lon")
        assertTrue(result is RepoResult.Error)
        assertTrue((result as RepoResult.Error).message.contains("Network error"))
    }

    @Test
    fun `searchCities returns Error on HttpException`() = runTest {
        val httpException = HttpException(
            Response.error<Any>(
                500,
                "{}".toResponseBody("application/json".toMediaTypeOrNull()),
            ),
        )
        coEvery { geocodingApi.search(query = "lon", any(), any(), any()) } throws httpException
        val result = repository.searchCities("lon")
        assertTrue(result is RepoResult.Error)
        assertTrue((result as RepoResult.Error).message.contains("Server error"))
    }

    @Test
    fun `getWeather returns Success when current weather exists`() = runTest {
        val city = City("London", "UK", 51.5, -0.1)
        coEvery { weatherApi.getCurrentWeather(latitude = 51.5, longitude = -0.1, any(), any()) } returns WeatherResponseDto(
            current = CurrentWeatherDto(
                temperatureCelsius = 20.0,
                humidity = 55,
                windSpeedMs = 3.2,
                pressureHpa = 1012.0,
                weatherCode = 1,
            ),
        )

        val result = repository.getWeather(city)
        assertTrue(result is RepoResult.Success)
        val data = (result as RepoResult.Success).data
        assertEquals("London", data.cityName)
        assertEquals("Mainly clear", data.condition)
    }

    @Test
    fun `getWeather returns Empty when current is missing`() = runTest {
        val city = City("London", "UK", 51.5, -0.1)
        coEvery { weatherApi.getCurrentWeather(latitude = 51.5, longitude = -0.1, any(), any()) } returns WeatherResponseDto(
            current = null,
        )

        val result = repository.getWeather(city)
        assertTrue(result is RepoResult.Empty)
    }

    @Test
    fun `getWeather returns Error on IOException`() = runTest {
        val city = City("London", "UK", 51.5, -0.1)
        coEvery { weatherApi.getCurrentWeather(latitude = 51.5, longitude = -0.1, any(), any()) } throws IOException("no internet")

        val result = repository.getWeather(city)
        assertTrue(result is RepoResult.Error)
        assertTrue((result as RepoResult.Error).message.contains("Network error"))
    }

    @Test
    fun `getWeather returns Error on HttpException`() = runTest {
        val city = City("London", "UK", 51.5, -0.1)
        val httpException = HttpException(
            Response.error<Any>(
                500,
                "{}".toResponseBody("application/json".toMediaTypeOrNull()),
            ),
        )
        coEvery { weatherApi.getCurrentWeather(latitude = 51.5, longitude = -0.1, any(), any()) } throws httpException

        val result = repository.getWeather(city)
        assertTrue(result is RepoResult.Error)
        assertTrue((result as RepoResult.Error).message.contains("Server error"))
    }
}
