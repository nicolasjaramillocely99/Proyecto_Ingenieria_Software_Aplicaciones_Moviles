package com.example.vinylsapp.ui.collectors

import com.example.vinylsapp.data.model.Collector

/**
 * Representa los distintos estados de la pantalla de coleccionistas.
 */
data class CollectorUiState(
    val collectors: List<Collector> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)
