package com.weathersnap.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.weathersnap.data.local.dao.ReportDao
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.data.local.weatherCacheDataStore
import com.weathersnap.data.remote.api.GeocodingApi
import com.weathersnap.data.remote.api.WeatherApi
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import com.weathersnap.util.WeatherCodeMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val reportDao: ReportDao,
    private val gson: Gson,
    @ApplicationContext private val context: Context,
) : WeatherRepository {

    override suspend fun searchCities(query: String): RepoResult<List<City>> {
        return try {
            val response = geocodingApi.search(query = query)
            val cities = response.results.orEmpty().mapNotNull { dto ->
                val name = dto.name ?: return@mapNotNull null
                val country = dto.country ?: return@mapNotNull null
                val lat = dto.latitude ?: return@mapNotNull null
                val lon = dto.longitude ?: return@mapNotNull null
                City(name = name, country = country, latitude = lat, longitude = lon)
            }
            if (cities.isEmpty()) RepoResult.Empty else RepoResult.Success(cities)
        } catch (e: IOException) {
            RepoResult.Error("Network error. Please check your connection.", e)
        } catch (e: HttpException) {
            RepoResult.Error("Server error (${e.code()}). Please try again.", e)
        }
    }

    override suspend fun getWeather(city: City): RepoResult<WeatherData> {
        return try {
            val response = weatherApi.getCurrentWeather(
                latitude = city.latitude,
                longitude = city.longitude,
            )
            val current = response.current ?: return RepoResult.Empty
            val temp = current.temperatureCelsius ?: return RepoResult.Empty
            val humidity = current.humidity ?: return RepoResult.Empty
            val wind = current.windSpeedMs ?: return RepoResult.Empty
            val pressure = current.pressureHpa ?: return RepoResult.Empty
            val code = current.weatherCode ?: return RepoResult.Empty
            val condition = WeatherCodeMapper.toConditionString(code)

            RepoResult.Success(
                WeatherData(
                    cityName = city.name,
                    country = city.country,
                    condition = condition,
                    temperatureCelsius = temp,
                    humidity = humidity,
                    windSpeedMs = wind,
                    pressureHpa = pressure,
                    latitude = city.latitude,
                    longitude = city.longitude,
                ),
            )
        } catch (e: IOException) {
            RepoResult.Error("Network error. Please check your connection.", e)
        } catch (e: HttpException) {
            RepoResult.Error("Server error (${e.code()}). Please try again.", e)
        }
    }

    override fun getAllReports(): Flow<List<ReportEntity>> =
        reportDao.getAllReports().flowOn(Dispatchers.IO)

    override suspend fun insertReport(report: ReportEntity): RepoResult<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                reportDao.insert(report)
            }
            RepoResult.Success(Unit)
        } catch (e: Exception) {
            RepoResult.Error("Unable to save report. Please try again.", e)
        }
    }

    override suspend fun saveCachedWeather(cityKey: String, weatherData: WeatherData) {
        val key = stringPreferencesKey("weather_${cityKey.hashCode()}")
        val json = gson.toJson(weatherData)
        context.weatherCacheDataStore.edit { prefs ->
            prefs[key] = json
        }
    }

    override suspend fun loadCachedWeather(cityKey: String): WeatherData? {
        val key = stringPreferencesKey("weather_${cityKey.hashCode()}")
        return withContext(Dispatchers.IO) {
            val first = context.weatherCacheDataStore.data.firstOrNull() ?: return@withContext null
            val json = first[key] ?: return@withContext null
            runCatching { gson.fromJson(json, WeatherData::class.java) }.getOrNull()
        }
    }
}
