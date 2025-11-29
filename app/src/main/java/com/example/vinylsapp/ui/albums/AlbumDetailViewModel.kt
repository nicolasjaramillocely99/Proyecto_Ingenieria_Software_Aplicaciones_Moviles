package com.example.vinylsapp.ui.albums

import androidx.lifecycle.SavedStateHandle
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
 * ViewModel para la pantalla de detalle de álbum (Patrón MVVM)
 * Gestiona el estado de la UI y la lógica de negocio
 * Se comunica con el Repository para obtener datos
 */
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Obtener el ID del álbum desde los argumentos de navegación
    private val albumId: Int = savedStateHandle.get<Int>("albumId") ?: 0
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar detalles del álbum al iniciar el ViewModel
        if (albumId > 0) {
            loadAlbumDetails()
        }
        
        // Observar cambios en savedStateHandle para refrescar cuando se crea un track
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Boolean?>("track_created", null)
                .collect { trackCreated ->
                    if (trackCreated == true) {
                        loadAlbumDetails()
                        savedStateHandle.remove<Boolean>("track_created")
                    }
                }
        }
    }
    
    /**
     * Carga los detalles del álbum desde el repository
     * Actualiza el estado de la UI según el resultado
     */
    fun loadAlbumDetails() {
        if (albumId <= 0) return
        
        viewModelScope.launch {
            repository.getAlbumById(albumId).collect { result ->
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
                                album = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    
                    is Result.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Selecciona una canción de la lista
     * Resalta visualmente la canción seleccionada
     * 
     * @param trackId ID de la canción seleccionada
     */
    fun selectTrack(trackId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTrackId = if (currentState.selectedTrackId == trackId) null else trackId
            )
        }
    }
}
