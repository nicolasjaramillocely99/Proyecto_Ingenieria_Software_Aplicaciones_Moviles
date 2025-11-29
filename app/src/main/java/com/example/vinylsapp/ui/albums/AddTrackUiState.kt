package com.example.vinylsapp.ui.albums

/**
 * Estado de la UI para la pantalla de agregar track
 * Gestiona el estado del formulario y los posibles errores
 */
data class AddTrackUiState(
    val name: String = "",
    val duration: String = "",
    val number: String = "",
    val composer: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    /**
     * Valida si el formulario está completo y válido
     * Solo el nombre es obligatorio
     */
    fun isValid(): Boolean {
        return name.isNotBlank()
    }
    
    /**
     * Convierte la duración en formato MM:SS a segundos
     * Retorna null si el formato no es válido
     */
    fun getDurationInSeconds(): Int? {
        if (duration.isBlank()) return null
        
        return try {
            val parts = duration.split(":")
            when (parts.size) {
                2 -> {
                    val minutes = parts[0].toInt()
                    val seconds = parts[1].toInt()
                    minutes * 60 + seconds
                }
                3 -> {
                    val hours = parts[0].toInt()
                    val minutes = parts[1].toInt()
                    val seconds = parts[2].toInt()
                    hours * 3600 + minutes * 60 + seconds
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Obtiene el número de pista como Int, o null si está vacío o no es válido
     */
    fun getTrackNumber(): Int? {
        return if (number.isBlank()) null else number.toIntOrNull()
    }
}

