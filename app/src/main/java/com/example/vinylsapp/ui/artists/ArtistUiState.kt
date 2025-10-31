package com.example.vinylsapp.ui.artists

import com.example.vinylsapp.data.model.Musician

/**
 * Estado de la UI para la pantalla de músicos
 * Representa todos los posibles estados de la interfaz
 */
data class ArtistUiState(
    val artists: List<Musician> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)
