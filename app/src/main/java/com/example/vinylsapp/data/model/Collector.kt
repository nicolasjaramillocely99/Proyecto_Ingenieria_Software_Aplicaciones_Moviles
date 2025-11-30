package com.example.vinylsapp.data.model

/**
 * Modelo de datos que representa a un coleccionista dentro de la aplicación.
 */
data class Collector(
    val id: Int,
    val name: String,
    val avatarUrl: String = "",
    val country: String = "",
    val city: String = "",
    val shortBio: String = "",
    val totalAlbums: Int = 0,
    val telephone: String = "",
    val email: String = "",
    val favoriteGenres: List<String> = emptyList(),
    val favoriteArtists: List<String> = emptyList(),
    val featuredAlbums: List<FeaturedAlbum> = emptyList()
)

data class FeaturedAlbum(
    val id: Int,
    val title: String,
    val artist: String,
    val coverUrl: String
)