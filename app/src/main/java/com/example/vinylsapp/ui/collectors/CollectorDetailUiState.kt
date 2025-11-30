package com.example.vinylsapp.ui.collectors

import com.example.vinylsapp.data.model.Collector

/**
 * Estado de UI para la pantalla de detalle de coleccionista.
 */
data class CollectorDetailUiState(
    val collector: Collector? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
