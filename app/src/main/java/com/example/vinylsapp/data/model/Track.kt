package com.example.vinylsapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar una canción/track de un álbum
 * Basado en la estructura de la entidad Track del backend
 */
data class Track(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("duration")
    val duration: String, // Formato: "MM:SS" o "HH:MM:SS"
    
    @SerializedName("albumId")
    val albumId: Int? = null
)

