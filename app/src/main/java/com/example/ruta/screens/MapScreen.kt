package com.example.ruta.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ruta.viewmodels.MapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.util.GeoPoint

@Composable
fun MapScreen(viewModel: MapViewModel, homeLocation: Pair<Double, Double>?) {
    // Observamos los puntos de la ruta desde el ViewModel
    val points = viewModel.routePoints.value

    // AndroidView permite usar vistas clásicas (XML/Views) dentro de Compose
    AndroidView(
        factory = { context ->
            // Se ejecuta una sola vez cuando se crea la vista
            MapView(context).apply {
                // Configuración básica del mapa (OpenStreetMap - Mapnik)
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true) // Habilita zoom con pellizco
                controller.setZoom(15.0)

                // Si existe una ubicación de casa guardada, centramos el mapa y ponemos un marcador
                homeLocation?.let {
                    val homePoint = GeoPoint(it.first, it.second)
                    controller.setCenter(homePoint)

                    val marker = Marker(this)
                    marker.position = homePoint
                    marker.title = "Mi Casa"
                    overlays.add(marker) // Añade el marcador a la lista de capas del mapa
                }
            }
        },
        update = { mapView ->
            // Se ejecuta cada vez que el estado de Compose cambia (ej. nuevos puntos de ruta)
            if (points.isNotEmpty()) {
                // Limpiamos líneas anteriores para no dibujar una encima de otra
                mapView.overlays.removeIf { it is Polyline }

                // Creamos la línea de la ruta (Polyline)
                val line = Polyline(mapView)
                line.setPoints(points) // 'points' debe ser una lista de GeoPoint

                mapView.overlays.add(line)

                // Forzamos el redibujado del mapa para mostrar los cambios
                mapView.invalidate()
            }
        },
        modifier = Modifier.fillMaxSize() // El mapa ocupa toda la pantalla
    )
}