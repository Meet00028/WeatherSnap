package com.weathersnap.data.repository

import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun searchCities(query: String): RepoResult<List<City>>
    suspend fun getWeather(city: City): RepoResult<WeatherData>

    fun getAllReports(): Flow<List<ReportEntity>>
    suspend fun insertReport(report: ReportEntity): RepoResult<Unit>

    suspend fun saveCachedWeather(cityKey: String, weatherData: WeatherData)
    suspend fun loadCachedWeather(cityKey: String): WeatherData?
}

