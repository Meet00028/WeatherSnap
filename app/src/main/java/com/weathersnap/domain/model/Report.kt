package com.weathersnap.domain.model

data class Report(
    val id: Int,
    val weather: WeatherData,
    val originalImagePath: String,
    val compressedImagePath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val notes: String,
    val savedAt: Long,
)

