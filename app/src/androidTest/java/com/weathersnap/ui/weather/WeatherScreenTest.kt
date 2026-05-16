package com.weathersnap.ui.weather

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.data.repository.RepoResult
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import com.weathersnap.ui.theme.WeatherSnapTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun cityInput_showsSuggestions_andLoadsWeatherCard() {
        val repo = object : WeatherRepository {
            override suspend fun searchCities(query: String): RepoResult<List<City>> =
                RepoResult.Success(listOf(City("London", "UK", 51.5, -0.1)))

            override suspend fun getWeather(city: City): RepoResult<WeatherData> =
                RepoResult.Success(
                    WeatherData(
                        cityName = city.name,
                        country = city.country,
                        condition = "Clear sky",
                        temperatureCelsius = 18.0,
                        humidity = 45,
                        windSpeedMs = 2.0,
                        pressureHpa = 1010.0,
                        latitude = city.latitude,
                        longitude = city.longitude,
                    ),
                )

            override fun getAllReports(): Flow<List<ReportEntity>> = flowOf(emptyList())
            override suspend fun insertReport(report: ReportEntity): RepoResult<Unit> = RepoResult.Success(Unit)
            override suspend fun saveCachedWeather(cityKey: String, weatherData: WeatherData) = Unit
            override suspend fun loadCachedWeather(cityKey: String): WeatherData? = null
        }

        val vm = WeatherViewModel(repository = repo, gson = Gson())

        composeRule.setContent {
            WeatherSnapTheme {
                WeatherScreen(navController = rememberNavController(), viewModel = vm)
            }
        }

        composeRule.onNodeWithTag(WeatherScreenTags.CityInput).performTextInput("Lon")

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(WeatherScreenTags.SuggestionsList).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("London").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(WeatherScreenTags.WeatherCard).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag(WeatherScreenTags.WeatherCard).assertIsDisplayed()
        composeRule.onNodeWithText("Create Report").assertIsDisplayed()
    }
}
