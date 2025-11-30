package com.example.vinylsapp.ui.collectors

import androidx.lifecycle.SavedStateHandle
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

@HiltViewModel
class CollectorDetailViewModel @Inject constructor(
    private val repository: CollectorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val collectorId: Int = savedStateHandle["collectorId"] ?: 0

    private val _uiState = MutableStateFlow(CollectorDetailUiState(isLoading = true))
    val uiState: StateFlow<CollectorDetailUiState> = _uiState.asStateFlow()

    init {
        loadCollector()
    }

    fun loadCollector() {
        viewModelScope.launch {
            repository.getCollectorDetail(collectorId).collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Result.Success -> _uiState.update {
                        it.copy(
                            collector = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Result.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error desconocido"
                        )
                    }
                }
            }
        }
    }
}
