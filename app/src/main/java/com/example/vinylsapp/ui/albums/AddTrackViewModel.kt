package com.example.vinylsapp.ui.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylsapp.data.model.CreateTrackRequest
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
 * ViewModel para la pantalla de agregar track (Patrón MVVM)
 * Gestiona el estado de la UI y la lógica de negocio
 * Se comunica con el Repository para crear tracks
 */
@HiltViewModel
class AddTrackViewModel @Inject constructor(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Obtener el ID del álbum desde los argumentos de navegación
    private val albumId: Int = savedStateHandle.get<Int>("albumId") ?: 0
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(AddTrackUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<AddTrackUiState> = _uiState.asStateFlow()
    
    /**
     * Actualiza el nombre del track
     */
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }
    
    /**
     * Actualiza la duración del track
     */
    fun updateDuration(duration: String) {
        _uiState.update { it.copy(duration = duration, error = null) }
    }
    
    /**
     * Actualiza el número de pista
     */
    fun updateNumber(number: String) {
        _uiState.update { it.copy(number = number, error = null) }
    }
    
    /**
     * Actualiza el compositor o artista colaborador
     */
    fun updateComposer(composer: String) {
        _uiState.update { it.copy(composer = composer, error = null) }
    }
    
    /**
     * Crea un nuevo track asociado al álbum
     * @param onSuccess Callback que se ejecuta cuando el track se crea exitosamente
     */
    fun createTrack(onSuccess: () -> Unit) {
        if (albumId <= 0) {
            _uiState.update { it.copy(error = "ID de álbum inválido") }
            return
        }
        
        if (!_uiState.value.isValid()) {
            _uiState.update { it.copy(error = "El nombre del track es obligatorio") }
            return
        }
        
        val currentState = _uiState.value
        
        val trackRequest = CreateTrackRequest(
            name = currentState.name.trim(),
            duration = currentState.duration.takeIf { it.isNotBlank() },
            albumId = albumId,
            seconds = currentState.getDurationInSeconds(),
            number = currentState.getTrackNumber(),
            composer = currentState.composer.takeIf { it.isNotBlank() }
        )
        
        viewModelScope.launch {
            repository.createTrack(albumId, trackRequest).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = true,
                                error = null,
                                isSuccess = false
                            )
                        }
                    }
                    
                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true
                            )
                        }
                        onSuccess()
                    }
                    
                    is Result.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido al crear el track",
                                isSuccess = false
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Limpia el estado de éxito
     */
    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

