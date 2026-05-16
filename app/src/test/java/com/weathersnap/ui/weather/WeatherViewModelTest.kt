package com.weathersnap.ui.weather

import app.cash.turbine.test
import com.google.gson.Gson
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.data.repository.RepoResult
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @Test
    fun `onSuggestionSelected emits Loading then Success`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
        val city = City("London", "UK", 51.5, -0.1)
        val repo = FakeWeatherRepository(
            weatherResult = RepoResult.Success(
                WeatherData(
                    cityName = "London",
                    country = "UK",
                    condition = "Clear sky",
                    temperatureCelsius = 18.4,
                    humidity = 45,
                    windSpeedMs = 2.1,
                    pressureHpa = 1011.0,
                    latitude = 51.5,
                    longitude = -0.1,
                ),
            ),
        )

        val vm = WeatherViewModel(repository = repo, gson = Gson())

        vm.uiState.test {
            assertTrue(awaitItem() is WeatherUiState.Idle)
            vm.onSuggestionSelected(city)
            assertTrue(awaitItem() is WeatherUiState.Loading)
            val success = awaitItem()
            assertTrue(success is WeatherUiState.Success)
            assertEquals(false, (success as WeatherUiState.Success).isCached)
        }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `city query debounce triggers suggestions`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
        val repo = FakeWeatherRepository(
            cityResult = RepoResult.Success(listOf(City("London", "UK", 51.5, -0.1))),
        )
        val vm = WeatherViewModel(repository = repo, gson = Gson())

        vm.suggestions.test {
            assertEquals(emptyList<City>(), awaitItem())
            vm.onCityQueryChange("Lon")
            advanceTimeBy(301)
            val next = awaitItem()
            assertEquals(1, next.size)
            assertEquals("London", next.first().name)
        }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `retry re-fetches weather for last city`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val city = City("London", "UK", 51.5, -0.1)
            val repo = FakeWeatherRepository(
                weatherResult = RepoResult.Success(
                    WeatherData(
                        cityName = "London",
                        country = "UK",
                        condition = "Clear sky",
                        temperatureCelsius = 18.4,
                        humidity = 45,
                        windSpeedMs = 2.1,
                        pressureHpa = 1011.0,
                        latitude = 51.5,
                        longitude = -0.1,
                    ),
                ),
            )
            val vm = WeatherViewModel(repository = repo, gson = Gson())
            vm.onSuggestionSelected(city)
            vm.uiState.test {
                awaitItem()
                vm.retry()
                assertTrue(awaitItem() is WeatherUiState.Loading)
                assertTrue(awaitItem() is WeatherUiState.Success)
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeWeatherRepository(
        private val cityResult: RepoResult<List<City>> = RepoResult.Empty,
        private val weatherResult: RepoResult<WeatherData> = RepoResult.Empty,
    ) : WeatherRepository {

        override suspend fun searchCities(query: String): RepoResult<List<City>> = cityResult

        override suspend fun getWeather(city: City): RepoResult<WeatherData> = weatherResult

        override fun getAllReports(): Flow<List<ReportEntity>> = flowOf(emptyList())

        override suspend fun insertReport(report: ReportEntity): RepoResult<Unit> = RepoResult.Success(Unit)

        override suspend fun saveCachedWeather(cityKey: String, weatherData: WeatherData) = Unit

        override suspend fun loadCachedWeather(cityKey: String): WeatherData? = null
    }
}
