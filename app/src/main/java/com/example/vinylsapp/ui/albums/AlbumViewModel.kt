package com.example.vinylsapp.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylsapp.data.repository.AlbumRepository
import com.example.vinylsapp.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de álbumes (Patrón MVVM)
 * Gestiona el estado de la UI y la lógica de negocio
 * Se comunica con el Repository para obtener datos
 */
@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(AlbumUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar álbumes al iniciar el ViewModel
        loadAlbums()
    }
    
    /**
     * Carga la lista de álbumes desde el repository
     * Actualiza el estado de la UI según el resultado
     */
    fun loadAlbums() {
        viewModelScope.launch {
            repository.getAlbums().collect { result ->
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
                                albums = result.data,
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
                                isEmpty = currentState.albums.isEmpty()
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
