package com.weathersnap.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    fun formatKb(bytes: Long): String {
        val kb = bytes.toDouble() / 1024.0
        return String.format(Locale.US, "%.1f", kb)
    }

    fun formatTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        return sdf.format(Date(millis)).lowercase(Locale.US)
    }
}
