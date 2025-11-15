package com.example.vinylsapp.ui.albums

/**
 * Estado de la UI para la pantalla de crear álbum
 * Gestiona el estado del formulario y los posibles errores
 */
data class CreateAlbumUiState(
    val name: String = "",
    val cover: String = "",
    val releaseDate: String = "",
    val description: String = "",
    val genre: String = "",
    val recordLabel: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isGenreDropdownExpanded: Boolean = false,
    val isRecordLabelDropdownExpanded: Boolean = false
) {
    /**
     * Valida si el formulario está completo y válido
     */
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                cover.isNotBlank() &&
                releaseDate.isNotBlank() &&
                description.isNotBlank() &&
                genre.isNotBlank() &&
                recordLabel.isNotBlank()
    }
    
    /**
     * Opciones válidas de género según el backend
     */
    companion object {
        val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")
        val recordLabelOptions = listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")
    }
}

