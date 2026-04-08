package com.example.ruta.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase principal que representa la respuesta completa de la API.
 * @SerializedName vincula el nombre de la variable en Kotlin con la clave del JSON.
 */
data class RouteResponse(
    @SerializedName("features") val features: List<Feature>
)

/**
 * Cada "Feature" representa un elemento del mapa, en este caso, la ruta calculada.
 */
data class Feature(
    @SerializedName("geometry") val geometry: Geometry
)

/**
 * Contiene la información geográfica de la ruta.
 */
data class Geometry(
    /**
     * Lista de puntos que forman la línea de la ruta.
     * IMPORTANTE: En el estándar GeoJSON (usado por ORS), el orden es [Longitud, Latitud].
     * Google Maps usa [Latitud, Longitud], por lo que deberás invertirlos al dibujar.
     */
    @SerializedName("coordinates") val coordinates: List<List<Double>>
)