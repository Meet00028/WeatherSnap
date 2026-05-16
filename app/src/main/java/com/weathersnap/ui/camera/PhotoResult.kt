package com.weathersnap.ui.camera

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoResult(
    val originalPath: String,
    val compressedPath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
) : Parcelable
