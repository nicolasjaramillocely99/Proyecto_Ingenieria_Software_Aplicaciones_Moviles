package com.example.vinylsapp.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

/**
 * Service Adapter Pattern: Interface de Retrofit para el API de coleccionistas
 */
fun interface CollectorApiService {

    /**
     * Obtiene el listado de coleccionistas
     * Endpoint: GET /collectors
     */
    @GET("collectors")
    suspend fun getCollectors(): Response<List<CollectorDto>>
}

/**
 * Representación de la respuesta del endpoint de coleccionistas
 */
data class CollectorDto(
    val id: Int,
    val name: String,
    val telephone: String? = null,
    val email: String? = null,
    val comments: List<CollectorCommentDto>? = emptyList(),
    val favoritePerformers: List<CollectorPerformerDto>? = emptyList(),
    val collectorAlbums: List<CollectorAlbumDto>? = emptyList()
)

data class CollectorCommentDto(
    val id: Int,
    val description: String? = null,
    val rating: Int? = null
)

data class CollectorPerformerDto(
    val id: Int,
    val name: String? = null,
    val image: String? = null,
    val description: String? = null,
    @SerializedName("birthDate") val birthDate: String? = null,
    @SerializedName("creationDate") val creationDate: String? = null
)

data class CollectorAlbumDto(
    val id: Int,
    val price: Int? = null,
    val status: String? = null
)
