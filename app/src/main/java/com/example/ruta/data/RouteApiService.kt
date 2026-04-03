package com.example.ruta.data.repository

import com.example.ruta.data.model.RouteResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteApiService {
    @GET("v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") apiKey: String,
        @Query("start") start: String, // "long,lat"
        @Query("end") end: String      // "long,lat"
    ): RouteResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    val instance: RouteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // CAMBIA GsonFactory por GsonConverterFactory
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouteApiService::class.java)
    }
}