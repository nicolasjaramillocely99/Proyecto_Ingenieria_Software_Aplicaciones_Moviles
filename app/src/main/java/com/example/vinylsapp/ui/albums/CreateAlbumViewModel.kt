package com.example.vinylsapp.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylsapp.data.model.CreateAlbumRequest
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
 * ViewModel para la pantalla de crear álbum (Patrón MVVM)
 * Gestiona el estado del formulario y la lógica de creación
 */
@HiltViewModel
class CreateAlbumViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(CreateAlbumUiState())
    
    // Estado público inmutable para la UI
    val uiState: StateFlow<CreateAlbumUiState> = _uiState.asStateFlow()
    
    /**
     * Actualiza el nombre del álbum
     */
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }
    
    /**
     * Actualiza la URL de la portada
     */
    fun updateCover(cover: String) {
        _uiState.update { it.copy(cover = cover, error = null) }
    }
    
    /**
     * Actualiza la fecha de lanzamiento
     */
    fun updateReleaseDate(releaseDate: String) {
        _uiState.update { it.copy(releaseDate = releaseDate, error = null) }
    }
    
    /**
     * Actualiza la descripción
     */
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description, error = null) }
    }
    
    /**
     * Actualiza el género
     */
    fun updateGenre(genre: String) {
        _uiState.update { it.copy(genre = genre, error = null) }
    }
    
    /**
     * Controla el estado de expansión del dropdown de género
     */
    fun setGenreDropdownExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isGenreDropdownExpanded = expanded) }
    }
    
    /**
     * Actualiza la discográfica
     */
    fun updateRecordLabel(recordLabel: String) {
        _uiState.update { it.copy(recordLabel = recordLabel, error = null) }
    }
    
    /**
     * Controla el estado de expansión del dropdown de discográfica
     */
    fun setRecordLabelDropdownExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isRecordLabelDropdownExpanded = expanded) }
    }
    
    /**
     * Crea un nuevo álbum
     * Retorna true si la creación fue exitosa, false en caso contrario
     */
    fun createAlbum(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (!currentState.isValid()) {
            _uiState.update { 
                it.copy(error = "Por favor completa todos los campos") 
            }
            return
        }
        
        viewModelScope.launch {
            val request = CreateAlbumRequest(
                name = currentState.name.trim(),
                cover = currentState.cover.trim(),
                releaseDate = currentState.releaseDate.trim(),
                description = currentState.description.trim(),
                genre = currentState.genre.trim(),
                recordLabel = currentState.recordLabel.trim()
            )
            
            repository.createAlbum(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { 
                            it.copy(isLoading = true, error = null, isSuccess = false) 
                        }
                    }
                    
                    is Result.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true
                            ) 
                        }
                        onSuccess()
                    }
                    
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error al crear el álbum",
                                isSuccess = false
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
    
    /**
     * Resetea el estado del formulario
     */
    fun resetState() {
        _uiState.value = CreateAlbumUiState()
    }
}

