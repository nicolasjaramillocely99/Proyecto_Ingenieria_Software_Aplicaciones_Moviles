package com.example.vinylsapp.ui.albums

import com.example.vinylsapp.data.model.Album

/**
 * Estado de la UI para la pantalla de álbumes
 * Representa todos los posibles estados de la interfaz
 */
data class AlbumUiState(
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)
