package com.weathersnap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val country: String,
    val condition: String,
    val temperatureCelsius: Double,
    val humidity: Int,
    val windSpeedMs: Double,
    val pressureHpa: Double,
    val originalImagePath: String,
    val compressedImagePath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val notes: String,
    val savedAt: Long = System.currentTimeMillis(),
)

