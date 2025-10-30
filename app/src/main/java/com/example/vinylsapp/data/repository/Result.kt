package com.example.vinylsapp.data.repository

/**
 * Clase sellada para representar el resultado de una operación
 * Útil para manejar estados de éxito, error y carga
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
