package com.example.ruta.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruta.data.local.UserPreferences
import com.example.ruta.data.repository.RetrofitClient
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

/**
 * AndroidViewModel nos permite acceder al contexto de la aplicación,
 * necesario para inicializar UserPreferences.
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)

    // Estado observable para los puntos de la ruta. La UI se redibuja cuando cambia.
    val routePoints = mutableStateOf<List<GeoPoint>>(emptyList())

    // Estado para mostrar un indicador de carga (ProgressBar) en la UI
    val isLoading = mutableStateOf(false)

    /**
     * Lógica para obtener la ruta desde la API de OpenRouteService.
     */
    fun calculateRoute(currentLat: Double, currentLon: Double, homeLat: Double, homeLon: Double) {
        // Ejecutamos en el scope del ViewModel para que la petición se cancele si el VM se destruye
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Tu API Key (Deberías moverla a un lugar seguro en producción)
                val apiKey = "TU_API_KEY_AQUÍ"

                // Llamada a Retrofit.
                // RECORDAR: ORS usa formato "Longitud,Latitud" en el String.
                val response = RetrofitClient.instance.getRoute(
                    apiKey = apiKey,
                    start = "$currentLon,$currentLat",
                    end = "$homeLon,$homeLat"
                )

                // Transformamos la respuesta de la API (List<Double>) a GeoPoints de osmdroid
                // La API devuelve [Lon, Lat], por eso usamos it[1] para Lat y it[0] para Lon.
                val coords = response.features[0].geometry.coordinates.map {
                    GeoPoint(it[1], it[0])
                }

                // Actualizamos el estado. La UI de Compose reaccionará automáticamente.
                routePoints.value = coords

            } catch (e: Exception) {
                // En un caso real, aquí deberías notificar el error al usuario (ej. un Toast)
                e.printStackTrace()
            } finally {
                // Ocultamos el indicador de carga independientemente de si hubo éxito o error
                isLoading.value = false
            }
        }
    }
}