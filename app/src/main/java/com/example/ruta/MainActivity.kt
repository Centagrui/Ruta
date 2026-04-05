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
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        org.osmdroid.config.Configuration.getInstance().load(
            this,
            android.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName
        super.onCreate(savedInstanceState)
        val userPrefs = UserPreferences(this)
        val locationHelper = LocationHelper(this)

        setContent {
            val scope = rememberCoroutineScope()
            // Se declara una sola vez
            val homeLoc by userPrefs.homeLocation.collectAsState(initial = null)
            var showSettings by remember { mutableStateOf(false) }

            // Lanzador de permisos
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    scope.launch {
                        val current = locationHelper.getCurrentLocation()
                        if (current != null && homeLoc != null) {
                            viewModel.calculateRoute(current.latitude, current.longitude, homeLoc!!.first, homeLoc!!.second)
                        }
                    }
                }
            }

            // Pedir permisos al iniciar
            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            // Lógica automática: Si cambia la casa o inicia la app, calcula la ruta
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

            // Interfaz de usuario
            if (showSettings || homeLoc == null) {
                SettingsScreen { lat, lon ->
                    scope.launch {
                        userPrefs.saveHomeLocation(lat, lon)
                        showSettings = false
                    }
                }
            } else {
                MapScreen(viewModel, homeLoc)
            }
        }
    }
}