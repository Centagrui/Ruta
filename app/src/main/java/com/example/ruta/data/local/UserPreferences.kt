package com.example.ruta.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Se define fuera de la clase para asegurar que solo haya una instancia en toda la app (Singleton).
val Context.dataStore by preferencesDataStore(name = "settings_pref")

class UserPreferences(private val context: Context) {

    // Objeto  para definir las llaves de acceso a los datos
    companion object {
        // Definimos las llaves para Latitud y Longitud
        val HOME_LAT = doublePreferencesKey("home_lat")
        val HOME_LON = doublePreferencesKey("home_lon")
    }

    /**
     * Recupera la ubicación guardada como un flujo (Flow).
     * Si la latitud es 0.0, asume que no hay datos y retorna null.
     */
    val homeLocation: Flow<Pair<Double, Double>?> = context.dataStore.data.map { pref ->
        val lat = pref[HOME_LAT] ?: 0.0
        val lon = pref[HOME_LON] ?: 0.0

        // Lógica de negocio: Si lat es 0.0, devolvemos null (ubicación no configurada)
        if (lat != 0.0) Pair(lat, lon) else null
    }

    /**
     * Función suspenso para guardar las coordenadas de forma asíncrona.
     * Debe ser llamada dentro de una Corrutina.
     */
    suspend fun saveHomeLocation(lat: Double, lon: Double) {
        context.dataStore.edit { preferences ->
            preferences[HOME_LAT] = lat
            preferences[HOME_LON] = lon
        }
    }
}