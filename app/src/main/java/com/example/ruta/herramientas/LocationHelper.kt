package com.example.ruta.herramientas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Agregamos esta anotación para decirle a Android que ya validamos los permisos
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): android.location.Location? {
        return try {
            if (hasLocationPermission()) {
                // El .await() requiere la dependencia de coroutines-play-services
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            } else {
                null
            }
        } catch (e: SecurityException) {
            // Manejamos la excepción en caso de que el permiso sea revocado
            null
        }
    }
}