package com.example.vinylsapp.ui.artists

import com.example.vinylsapp.data.model.Musician

/**
 * Estado de la UI para la pantalla de músicos
 * Representa todos los posibles estados de la interfaz
 */
data class ArtistDetailUiState(
    val artist: Musician? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
