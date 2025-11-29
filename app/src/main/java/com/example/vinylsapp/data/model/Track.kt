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
    val duration: String? = null, // Formato: "MM:SS" o "HH:MM:SS"
    
    @SerializedName("albumId")
    val albumId: Int? = null,
    
    @SerializedName("seconds")
    val seconds: Int? = null, // Duración en segundos (alternativa)
    
    @SerializedName("number")
    val number: Int? = null, // Número de pista
    
    @SerializedName("composer")
    val composer: String? = null // Compositor o artista colaborador
)

/**
 * Modelo de datos para crear un nuevo track
 * No incluye el campo id ya que es generado por el backend
 */
data class CreateTrackRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("duration")
    val duration: String? = null, // Formato: "MM:SS" o "HH:MM:SS"
    
    @SerializedName("albumId")
    val albumId: Int,
    
    @SerializedName("seconds")
    val seconds: Int? = null, // Duración en segundos (alternativa)
    
    @SerializedName("number")
    val number: Int? = null, // Número de pista
    
    @SerializedName("composer")
    val composer: String? = null // Compositor o artista colaborador
)

