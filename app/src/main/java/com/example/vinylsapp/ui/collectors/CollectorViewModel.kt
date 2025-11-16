package com.example.vinylsapp.ui.collectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylsapp.data.repository.CollectorRepository
import com.example.vinylsapp.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que implementa el patrón MVVM para la pantalla de coleccionistas.
 */
@HiltViewModel
class CollectorViewModel @Inject constructor(
    private val repository: CollectorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectorUiState(isLoading = true))
    val uiState: StateFlow<CollectorUiState> = _uiState.asStateFlow()

    init {
        loadCollectors()
    }

    fun loadCollectors() {
        viewModelScope.launch {
            repository.getCollectors().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }

                    is Result.Success -> {
                        _uiState.update { current ->
                            current.copy(
                                collectors = result.data,
                                isLoading = false,
                                error = null,
                                isEmpty = result.data.isEmpty()
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido",
                                isEmpty = current.collectors.isEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
