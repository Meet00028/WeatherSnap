package com.weathersnap.domain.model

data class WeatherData(
    val cityName: String,
    val country: String,
    val condition: String,
    val temperatureCelsius: Double,
    val humidity: Int,
    val windSpeedMs: Double,
    val pressureHpa: Double,
    val latitude: Double,
    val longitude: Double,
)

