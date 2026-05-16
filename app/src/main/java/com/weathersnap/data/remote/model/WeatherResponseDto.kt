package com.weathersnap.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("current") val current: CurrentWeatherDto?,
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperatureCelsius: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Int?,
    @SerializedName("wind_speed_10m") val windSpeedMs: Double?,
    @SerializedName("surface_pressure") val pressureHpa: Double?,
    @SerializedName("weather_code") val weatherCode: Int?,
)

