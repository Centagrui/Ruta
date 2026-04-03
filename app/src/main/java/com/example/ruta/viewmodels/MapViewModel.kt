package com.example.ruta.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruta.data.local.UserPreferences
import com.example.ruta.data.repository.RetrofitClient
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val userPrefs = UserPreferences(application)
    val routePoints = mutableStateOf<List<GeoPoint>>(emptyList())
    val isLoading = mutableStateOf(false)

    fun calculateRoute(currentLat: Double, currentLon: Double, homeLat: Double, homeLon: Double) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // IMPORTANTE: Pon tu API Key de OpenRouteService aquí
                val apiKey = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6ImQ2YzRkZGYyMTFmNjQ1ZTdiNjFmOTM1NGZkZmNhMTAyIiwiaCI6Im11cm11cjY0In0="
                val response = RetrofitClient.instance.getRoute(
                    apiKey = apiKey,
                    start = "$currentLon,$currentLat",
                    end = "$homeLon,$homeLat"
                )
                val coords = response.features[0].geometry.coordinates.map {
                    GeoPoint(it[1], it[0])
                }
                routePoints.value = coords
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}