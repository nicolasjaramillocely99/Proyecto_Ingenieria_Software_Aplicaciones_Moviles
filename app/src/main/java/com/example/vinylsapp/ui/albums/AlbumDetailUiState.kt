package com.example.vinylsapp.ui.albums

import com.example.vinylsapp.data.model.Album

/**
 * Estado de la UI para la pantalla de detalle de álbum
 * Representa todos los posibles estados de la interfaz
 */
data class AlbumDetailUiState(
    val album: Album? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTrackId: Int? = null // ID de la canción seleccionada/resaltada
)

