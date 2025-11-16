package com.example.vinylsapp.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylsapp.data.repository.MusicianRepository
import com.example.vinylsapp.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de detalle de artista (Patrón MVVM)
 * Gestiona el estado de la UI y la lógica de negocio
 * Se comunica con el Repository para obtener datos
 */
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: MusicianRepository
) : ViewModel() {
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Carga el artista desde el repository
     * Actualiza el estado de la UI según el resultado
     */
    fun loadArtist(musicianId: Int) {
        viewModelScope.launch {
            repository.getMusicianById(musicianId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                    
                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                artist = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    
                    is Result.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido",
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
