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
    val points = viewModel.routePoints.value

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)

                homeLocation?.let {
                    val homePoint = GeoPoint(it.first, it.second)
                    controller.setCenter(homePoint)
                    val marker = Marker(this)
                    marker.position = homePoint
                    marker.title = "Mi Casa"
                    overlays.add(marker)
                }
            }
        },
        update = { mapView ->
            if (points.isNotEmpty()) {
                mapView.overlays.removeIf { it is Polyline }
                val line = Polyline(mapView)
                line.setPoints(points)
                mapView.overlays.add(line)
                mapView.invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}