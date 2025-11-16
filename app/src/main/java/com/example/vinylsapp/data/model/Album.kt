package com.example.vinylsapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar un álbum
 * Basado en la estructura de la entidad Album del backend
 */
data class Album(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("cover")
    val cover: String,
    
    @SerializedName("releaseDate")
    val releaseDate: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("genre")
    val genre: String,
    
    @SerializedName("recordLabel")
    val recordLabel: String,
    
    @SerializedName("performers")
    val performers: List<Performer>? = null,
    
    @SerializedName("tracks")
    val tracks: List<Track>? = null
)

/**
 * Modelo para representar un artista/performer
 */
data class Performer(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image")
    val image: String? = null,
    
    @SerializedName("description")
    val description: String? = null
)

/**
 * Modelo de datos para crear un nuevo álbum
 * No incluye el campo id ya que es generado por el backend
 */
data class CreateAlbumRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("cover")
    val cover: String,
    
    @SerializedName("releaseDate")
    val releaseDate: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("genre")
    val genre: String,
    
    @SerializedName("recordLabel")
    val recordLabel: String
)