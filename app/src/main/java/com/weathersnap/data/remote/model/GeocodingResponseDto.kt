package com.weathersnap.data.remote.model

import com.google.gson.annotations.SerializedName

data class GeocodingResponseDto(
    @SerializedName("results") val results: List<GeocodingResultDto>?,
)

data class GeocodingResultDto(
    @SerializedName("name") val name: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
)

