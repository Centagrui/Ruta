package com.example.ruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        super.onCreate(savedInstanceState)
        val userPrefs = UserPreferences(this)
        val locationHelper = LocationHelper(this)

        setContent {
            val scope = rememberCoroutineScope()
            val homeLoc by userPrefs.homeLocation.collectAsState(initial = null)
            var showSettings by remember { mutableStateOf(false) }

            if (showSettings || homeLoc == null) {
                SettingsScreen { lat, lon ->
                    scope.launch { userPrefs.saveHomeLocation(lat, lon) }
                    showSettings = false
                }
            } else {
                LaunchedEffect(Unit) {
                    val current = locationHelper.getCurrentLocation()
                    if (current != null && homeLoc != null) {
                        viewModel.calculateRoute(current.latitude, current.longitude, homeLoc!!.first, homeLoc!!.second)
                    }
                }
                MapScreen(viewModel, homeLoc)
            }
        }
    }
}