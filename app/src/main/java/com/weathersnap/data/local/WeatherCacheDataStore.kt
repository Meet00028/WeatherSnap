package com.weathersnap.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.weatherCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_cache")

