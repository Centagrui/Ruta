package com.example.ruta

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ruta.data.local.UserPreferences
import com.example.ruta.herramientas.LocationHelper
import com.example.ruta.screens.MapScreen
import com.example.ruta.ui.theme.SettingsScreen
import com.example.ruta.viewmodels.MapViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Instanciamos el ViewModel usando 'by viewModels()' para que sobreviva a rotaciones
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- CONFIGURACIÓN DE OSMDROID ---
        // OpenStreetMap requiere cargar la configuración y un UserAgent único
        org.osmdroid.config.Configuration.getInstance().load(
            this,
            android.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName

        val userPrefs = UserPreferences(this)
        val locationHelper = LocationHelper(this)

        setContent {
            val scope = rememberCoroutineScope()

            // Convertimos el Flow de DataStore en un Estado de Compose
            val homeLoc by userPrefs.homeLocation.collectAsState(initial = null)

            // Estado local para alternar entre el Mapa y la Configuración
            var showSettings by remember { mutableStateOf(false) }

            // --- GESTIÓN DE PERMISOS ---
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                // Si el usuario acepta, intentamos calcular la ruta inmediatamente
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    scope.launch {
                        val current = locationHelper.getCurrentLocation()
                        if (current != null && homeLoc != null) {
                            viewModel.calculateRoute(
                                current.latitude, current.longitude,
                                homeLoc!!.first, homeLoc!!.second
                            )
                        }
                    }
                }
            }

            // --- EFECTOS LANZADOS (Side Effects) ---

            // 1. Pedir permisos al arrancar la aplicación
            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            // 2. Lógica reactiva: Si la ubicación de "Casa" cambia en DataStore,
            // recalculamos la ruta automáticamente.
            LaunchedEffect(homeLoc) {
                if (homeLoc != null) {
                    val current = locationHelper.getCurrentLocation()
                    if (current != null) {
                        viewModel.calculateRoute(
                            currentLat = current.latitude,
                            currentLon = current.longitude,
                            homeLat = homeLoc!!.first,
                            homeLon = homeLoc!!.second
                        )
                    }
                }
            }

            // --- NAVEGACIÓN BÁSICA ---
            // Si no hay una casa guardada o el usuario quiere editar, mostramos Settings
            if (showSettings || homeLoc == null) {
                SettingsScreen { lat, lon ->
                    scope.launch {
                        userPrefs.saveHomeLocation(lat, lon)
                        showSettings = false // Volvemos al mapa tras guardar
                    }
                }
            } else {
                // Si todo está listo, mostramos la pantalla del mapa
                MapScreen(viewModel, homeLoc)
            }
        }
    }
}