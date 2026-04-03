package com.example.ruta.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings_pref")

class UserPreferences(private val context: Context) {
    companion object {
        val HOME_LAT = doublePreferencesKey("home_lat")
        val HOME_LON = doublePreferencesKey("home_lon")
    }

    val homeLocation: Flow<Pair<Double, Double>?> = context.dataStore.data.map { pref ->
        val lat = pref[HOME_LAT] ?: 0.0
        val lon = pref[HOME_LON] ?: 0.0
        if (lat != 0.0) Pair(lat, lon) else null
    }

    suspend fun saveHomeLocation(lat: Double, lon: Double) {
        context.dataStore.edit { it[HOME_LAT] = lat; it[HOME_LON] = lon }
    }
}