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
 * ViewModel para la pantalla de artistas (Patrón MVVM)
 * Gestiona el estado de la UI y la lógica de negocio
 * Se comunica con el Repository para obtener datos
 */
@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: MusicianRepository
) : ViewModel() {
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(ArtistUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar artistas al iniciar el ViewModel
        loadArtists()
    }
    
    /**
     * Carga la lista de artistas desde el repository
     * Actualiza el estado de la UI según el resultado
     */
    fun loadArtists() {
        viewModelScope.launch {
            repository.getMusicians().collect { result ->
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
                                artists = result.data,
                                isLoading = false,
                                error = null,
                                isEmpty = result.data.isEmpty()
                            )
                        }
                    }
                    
                    is Result.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido",
                                isEmpty = currentState.artists.isEmpty()
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
