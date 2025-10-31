package com.example.vinylsapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar un músico
 * Basado en la estructura de la entidad Musician del backend
 */
data class Musician(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image")
    val image: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("birthDate")
    val birthDate: String,
)
