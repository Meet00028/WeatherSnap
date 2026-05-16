package com.weathersnap.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
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

class ReportsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emptyState_isShown_whenNoReports() {
        val vm = ReportsViewModel(repository = fakeRepoWithReports(flowOf(emptyList())))

        composeRule.setContent {
            WeatherSnapTheme {
                ReportsScreen(navController = rememberNavController(), viewModel = vm)
            }
        }

        composeRule.onNodeWithTag(ReportsScreenTags.EmptyState).assertIsDisplayed()
        composeRule.onNodeWithText("No reports yet. Create your first weather report.").assertIsDisplayed()
    }

    @Test
    fun list_isShown_whenReportsExist() {
        val report = ReportEntity(
            cityName = "London",
            country = "UK",
            condition = "Clear sky",
            temperatureCelsius = 18.0,
            humidity = 40,
            windSpeedMs = 2.0,
            pressureHpa = 1010.0,
            originalImagePath = "/tmp/original.jpg",
            compressedImagePath = "/tmp/compressed.jpg",
            originalSizeBytes = 1000,
            compressedSizeBytes = 600,
            notes = "Test note",
            savedAt = 1710000000000,
        )
        val vm = ReportsViewModel(repository = fakeRepoWithReports(flowOf(listOf(report))))

        composeRule.setContent {
            WeatherSnapTheme {
                ReportsScreen(navController = rememberNavController(), viewModel = vm)
            }
        }

        composeRule.onNodeWithTag(ReportsScreenTags.ReportsList).assertIsDisplayed()
        composeRule.onNodeWithText("London, UK").assertIsDisplayed()
        composeRule.onNodeWithText("Test note").assertIsDisplayed()
    }

    private fun fakeRepoWithReports(reportsFlow: Flow<List<ReportEntity>>): WeatherRepository =
        object : WeatherRepository {
            override suspend fun searchCities(query: String) = RepoResult.Empty
            override suspend fun getWeather(city: City) = RepoResult.Empty
            override fun getAllReports(): Flow<List<ReportEntity>> = reportsFlow
            override suspend fun insertReport(report: ReportEntity): RepoResult<Unit> = RepoResult.Success(Unit)
            override suspend fun saveCachedWeather(cityKey: String, weatherData: WeatherData) = Unit
            override suspend fun loadCachedWeather(cityKey: String): WeatherData? = null
        }
}
