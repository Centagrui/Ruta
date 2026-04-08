package com.example.ruta.data.repository

import com.example.ruta.data.model.RouteResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz que define los "endpoints" de la API.
 * Retrofit usará esto para generar las peticiones reales.
 */
interface RouteApiService {
    // Definimos una petición GET al endpoint de conducción de coches
    @GET("v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") apiKey: String, // Tu llave de desarrollador de ORS
        @Query("start") start: String,   // Punto de origen en formato "long,lat"
        @Query("end") end: String        // Punto de destino en formato "long,lat"
    ): RouteResponse // Mapea el JSON recibido a tu data class RouteResponse
}

/**
 * Singleton para gestionar la instancia de Retrofit.
 * Usamos 'object' en Kotlin para asegurar que solo exista una instancia (Patrón Singleton).
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    // 'by lazy' inicializa la instancia solo cuando se usa por primera vez
    val instance: RouteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // El conversor de Gson transforma el JSON automáticamente en objetos Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouteApiService::class.java)
    }
}