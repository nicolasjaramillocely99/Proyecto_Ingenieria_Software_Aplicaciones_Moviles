package com.example.vinylsapp.data.network

import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.CreateAlbumRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Service Adapter Pattern: Interface de Retrofit para el API de álbumes
 * Define los endpoints del backend para operaciones con álbumes
 */
interface AlbumApiService {
    
    /**
     * Obtiene la lista completa de álbumes
     * Endpoint: GET /albums
     */
    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>
    
    /**
     * Obtiene un álbum específico por su ID
     * Endpoint: GET /albums/{albumId}
     */
    @GET("albums/{albumId}")
    suspend fun getAlbumById(@Path("albumId") albumId: Int): Response<Album>
    
    /**
     * Crea un nuevo álbum
     * Endpoint: POST /albums
     */
    @POST("albums")
    suspend fun createAlbum(@Body album: CreateAlbumRequest): Response<Album>
}
