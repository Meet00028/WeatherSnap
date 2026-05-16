package com.weathersnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class CompressionResult(
    val originalFile: File,
    val compressedFile: File,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
)

object ImageCompressor {
    suspend fun compressJpeg60(
        context: Context,
        original: File,
    ): CompressionResult = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(original.absolutePath)
            ?: throw IllegalStateException("Unable to decode image")

        val compressedFile = File(context.filesDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(compressedFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, output)
        }

        CompressionResult(
            originalFile = original,
            compressedFile = compressedFile,
            originalSizeBytes = original.length(),
            compressedSizeBytes = compressedFile.length(),
        )
    }
}

