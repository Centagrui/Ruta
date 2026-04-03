package com.example.ruta.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onSave: (Double, Double) -> Unit) {
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Configurar Ubicación de Casa", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitud") })
        OutlinedTextField(value = lon, onValueChange = { lon = it }, label = { Text("Longitud") })
        Button(onClick = {
            val l1 = lat.toDoubleOrNull() ?: 0.0
            val l2 = lon.toDoubleOrNull() ?: 0.0
            onSave(l1, l2)
        }) {
            Text("Guardar Casa")
        }
    }
}