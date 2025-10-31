package com.example.vinylsapp.data.network

import com.example.vinylsapp.data.model.Musician
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Service Adapter Pattern: Interface de Retrofit para el API de musicos
 * Define los endpoints del backend para operaciones con musicos
 */
interface MusicianApiService {
    
    /**
     * Obtiene la lista completa de músicos
     * Endpoint: GET /musicians
     */
    @GET("musicians")
    suspend fun getMusicians(): Response<List<Musician>>
    
    /**
     * Obtiene un músico específica por su ID
     * Endpoint: GET /musicians/{musicianId}
     */
    @GET("musicians/{musicianId}")
    suspend fun getMusicianById(@Path("musicianId") musicianId: Int): Response<Musician>
}
