package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.CreateAlbumRequest
import com.example.vinylsapp.data.network.AlbumApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple logging utility that works in both unit tests and Android runtime
 */
internal object RepositoryLogger {
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e(tag, message, throwable)
        } catch (e: RuntimeException) {
            // Ignore if Log is not available (e.g., in unit tests)
        }
    }
    
    fun d(tag: String, message: String) {
        try {
            android.util.Log.d(tag, message)
        } catch (e: RuntimeException) {
            // Ignore if Log is not available (e.g., in unit tests)
        }
    }
}

/**
 * Repository Pattern: AlbumRepository
 * Actúa como única fuente de verdad para los datos de álbumes
 * Abstrae la lógica de obtención de datos del ViewModel
 */
@Singleton
class AlbumRepository @Inject constructor(
    private val apiService: AlbumApiService
) {
    
    /**
     * Obtiene la lista de álbumes desde el API
     * Retorna un Flow para permitir observación reactiva de los datos
     * 
     * @return Flow con Result que puede ser Success, Error o Loading
     */
    fun getAlbums(): Flow<Result<List<Album>>> = flow {
        try {
            // Emitir estado de carga
            emit(Result.Loading)
            
            // Hacer la petición al API
            val response = apiService.getAlbums()
            
            if (response.isSuccessful && response.body() != null) {
                // Éxito: emitir los datos
                emit(Result.Success(response.body()!!))
            } else {
                // Error en la respuesta
                emit(Result.Error(
                    exception = Exception("Error ${response.code()}"),
                    message = "Error al cargar los álbumes: ${response.message()}"
                ))
            }
        } catch (e: Exception) {
            // Error de red o parsing
            emit(Result.Error(
                exception = e,
                message = "Error de conexión: ${e.localizedMessage}"
            ))
        }
    }.flowOn(Dispatchers.IO) // Ejecutar en hilo de IO
    
    /**
     * Obtiene un álbum específico por su ID
     * 
     * @param albumId ID del álbum a obtener
     * @return Flow con Result que contiene el álbum o un error
     */
    fun getAlbumById(albumId: Int): Flow<Result<Album>> = flow {
        try {
            emit(Result.Loading)
            
            val response = apiService.getAlbumById(albumId)
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error(
                    exception = Exception("Error ${response.code()}"),
                    message = "Error al cargar el álbum"
                ))
            }
        } catch (e: Exception) {
            emit(Result.Error(
                exception = e,
                message = "Error de conexión: ${e.localizedMessage}"
            ))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Crea un nuevo álbum
     * 
     * @param album Datos del álbum a crear
     * @return Flow con Result que contiene el álbum creado o un error
     */
    fun createAlbum(album: CreateAlbumRequest): Flow<Result<Album>> = flow {
        try {
            emit(Result.Loading)
            
            val response = apiService.createAlbum(album)
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                // Intentar obtener el mensaje de error del body de la respuesta
                val errorBody = response.errorBody()?.string()
                
                RepositoryLogger.e("AlbumRepository", "=== CREATE ALBUM ERROR ===")
                RepositoryLogger.e("AlbumRepository", "Status Code: ${response.code()}")
                RepositoryLogger.e("AlbumRepository", "Status Message: ${response.message()}")
                RepositoryLogger.e("AlbumRepository", "Error Body Raw: $errorBody")
                
                val errorMessage = if (errorBody != null && errorBody.isNotBlank()) {
                    // Intentar extraer mensaje útil del error body
                    val extracted = extractErrorMessage(errorBody)
                    RepositoryLogger.e("AlbumRepository", "Extracted message: $extracted")
                    
                    if (extracted != null && extracted.isNotBlank() && extracted != "ValidationError" && !extracted.contains("ValidationError:")) {
                        extracted
                    } else {
                        // Si la extracción falla, intentar mostrar parte del error body de forma legible
                        val formatted = formatErrorForDisplay(errorBody)
                        if (formatted != null && formatted.isNotBlank() && formatted != "ValidationError") {
                            formatted
                        } else {
                            // Fallback: Si es un error de validación, dar un mensaje más útil
                            if (errorBody.contains("ValidationError", ignoreCase = true) || response.code() == 400) {
                                "Error de validación: Verifica que todos los campos sean correctos. El URL de la portada debe comenzar con http:// o https://"
                            } else {
                                "Error ${response.code()}: ${response.message()}"
                            }
                        }
                    }
                } else {
                    "Error ${response.code()}: ${response.message()}"
                }
                
                RepositoryLogger.e("AlbumRepository", "Final error message: $errorMessage")
                
                emit(Result.Error(
                    exception = Exception("Error ${response.code()}"),
                    message = errorMessage
                ))
            }
        } catch (e: HttpException) {
            // Manejar excepciones HTTP específicas
            val errorBody = e.response()?.errorBody()?.string()
            
            RepositoryLogger.e("AlbumRepository", "=== HTTP EXCEPTION ===")
            RepositoryLogger.e("AlbumRepository", "Status Code: ${e.code()}")
            RepositoryLogger.e("AlbumRepository", "Status Message: ${e.message()}")
            RepositoryLogger.e("AlbumRepository", "Error Body Raw: $errorBody")
            
            val errorMessage = if (errorBody != null && errorBody.isNotBlank()) {
                val extracted = extractErrorMessage(errorBody)
                RepositoryLogger.e("AlbumRepository", "Extracted message: $extracted")
                
                if (extracted != null && extracted.isNotBlank() && extracted != "ValidationError" && !extracted.contains("ValidationError:")) {
                    extracted
                } else {
                    val formatted = formatErrorForDisplay(errorBody)
                    if (formatted != null && formatted.isNotBlank() && formatted != "ValidationError") {
                        formatted
                    } else {
                        // Fallback: Si es un error de validación, dar un mensaje más útil
                        if (errorBody.contains("ValidationError", ignoreCase = true) || e.code() == 400) {
                            "Error de validación: Verifica que todos los campos sean correctos. El URL de la portada debe comenzar con http:// o https://"
                        } else {
                            "Error ${e.code()}: ${e.message()}"
                        }
                    }
                }
            } else {
                "Error ${e.code()}: ${e.message()}"
            }
            
            RepositoryLogger.e("AlbumRepository", "Final error message: $errorMessage", e)
            
            emit(Result.Error(
                exception = e,
                message = errorMessage
            ))
        } catch (e: Exception) {
            RepositoryLogger.e("AlbumRepository", "Exception creating album", e)
            emit(Result.Error(
                exception = e,
                message = "Error de conexión: ${e.message ?: e.localizedMessage ?: "Error desconocido"}"
            ))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Extrae un mensaje de error útil del cuerpo de respuesta JSON
     */
    private fun extractErrorMessage(errorBody: String): String? {
        return try {
            // Log the full error body for debugging
            RepositoryLogger.d("AlbumRepository", "Full error body: $errorBody")
            
            // Intentar extraer campos comunes de error como "message", "error", "detail"
            when {
                // Caso 1: Array de mensajes (común en validaciones NestJS)
                errorBody.contains("\"message\"") && errorBody.contains("[") -> {
                    // Formato: {"message": ["error1", "error2"]} o {"message": "error"}
                    val arrayRegex = "\"message\"\\s*:\\s*\\[\\s*\"([^\"]+)\"".toRegex()
                    val singleRegex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    arrayRegex.find(errorBody)?.groupValues?.get(1) 
                        ?: singleRegex.find(errorBody)?.groupValues?.get(1)
                }
                // Caso 2: Mensaje simple
                errorBody.contains("\"message\"") -> {
                    val messageRegex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex(RegexOption.MULTILINE)
                    messageRegex.find(errorBody)?.groupValues?.get(1)
                }
                // Caso 3: Campo "error"
                errorBody.contains("\"error\"") -> {
                    val errorRegex = "\"error\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    errorRegex.find(errorBody)?.groupValues?.get(1)
                }
                // Caso 4: Campo "detail"
                errorBody.contains("\"detail\"") -> {
                    val detailRegex = "\"detail\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    detailRegex.find(errorBody)?.groupValues?.get(1)
                }
                // Caso 5: Si contiene "ValidationError" o similar, intentar extraer el texto después
                errorBody.contains("ValidationError", ignoreCase = true) -> {
                    // Intentar múltiples patrones para ValidationError
                    // Patrón 1: ValidationError: "mensaje"
                    val pattern1 = "(?i)ValidationError[:\"\\s]+([^\\[\\{\"\\n]+)".toRegex()
                    // Patrón 2: "ValidationError": "mensaje"
                    val pattern2 = "\"ValidationError\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    // Patrón 3: ValidationError con formato de array
                    val pattern3 = "(?i)ValidationError[^\\[]*\\[([^\\]]+)\\]".toRegex()
                    // Patrón 4: Mensajes dentro de un objeto ValidationError
                    val pattern4 = "(?i)ValidationError.*?message[:\"\\s]+([^\\[\\{\"]+)".toRegex()
                    
                    pattern1.find(errorBody)?.groupValues?.get(1)?.trim()
                        ?: pattern2.find(errorBody)?.groupValues?.get(1)?.trim()
                        ?: pattern3.find(errorBody)?.groupValues?.get(1)?.trim()
                        ?: pattern4.find(errorBody)?.groupValues?.get(1)?.trim()
                        ?: extractNestedValidationMessages(errorBody)
                        ?: "Error de validación: Verifica que todos los campos sean correctos"
                }
                // Caso 6: Si parece ser un objeto de validación NestJS, extraer el primer mensaje útil
                errorBody.contains("statusCode") && errorBody.contains("error") -> {
                    // Formato NestJS estándar: {"statusCode": 400, "message": "...", "error": "..."}
                    val nestMessageRegex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    nestMessageRegex.find(errorBody)?.groupValues?.get(1)
                }
                // Caso 7: Si todo lo demás falla, retornar una parte del error body si es razonablemente corto
                errorBody.length < 200 -> {
                    // Si el error body es corto, mostrarlo directamente
                    errorBody.replace("\"", "").replace("{", "").replace("}", "").trim()
                }
                else -> null
            }
        } catch (e: Exception) {
            RepositoryLogger.e("AlbumRepository", "Error extracting error message", e)
            null
        }
    }
    
    /**
     * Formatea el error body para mostrarlo de forma legible si no se puede parsear
     */
    private fun formatErrorForDisplay(errorBody: String): String? {
        return try {
            // Si el error body es relativamente corto y parece contener información útil
            if (errorBody.length > 10 && errorBody.length < 500) {
                // Limpiar caracteres de escape y formato JSON básico
                var cleaned = errorBody
                    .replace("\\\"", "\"")  // Remover escapes de comillas
                    .replace("\\\\", "\\")  // Remover escapes de backslash
                    .replace("\\n", " ")    // Reemplazar newlines
                    .replace("\\t", " ")    // Reemplazar tabs
                    .replace("\\r", "")     // Remover carriage returns
                
                // Intentar encontrar texto útil después de ValidationError
                if (cleaned.contains("ValidationError", ignoreCase = true)) {
                    val parts = cleaned.split("ValidationError", ignoreCase = true)
                    if (parts.size > 1) {
                        val afterValidation = parts[1]
                            .replace(Regex("[{}\\[\\]\"'`]"), " ")
                            .replace(Regex("\\s+"), " ")
                            .trim()
                        
                        if (afterValidation.isNotBlank() && afterValidation.length < 200) {
                            return "Error de validación: $afterValidation"
                        }
                    }
                }
                
                // Si contiene un campo "message", extraerlo de forma simple
                val simpleMessageMatch = Regex("\"message\"\\s*:\\s*\"([^\"]+)\"").find(cleaned)
                simpleMessageMatch?.groupValues?.get(1)?.takeIf { it.isNotBlank() && it != "ValidationError" }
            } else {
                null
            }
        } catch (e: Exception) {
            RepositoryLogger.e("AlbumRepository", "Error formatting error for display", e)
            null
        }
    }
    
    /**
     * Extrae mensajes de validación anidados del error body
     */
    private fun extractNestedValidationMessages(errorBody: String): String? {
        return try {
            // Buscar arrays de mensajes de validación
            // Formato común: {"message": ["field1 debe ser...", "field2 debe ser..."]}
            val arrayPattern = "\"message\"\\s*:\\s*\\[([^\\]]+)\\]".toRegex()
            val arrayMatch = arrayPattern.find(errorBody)
            
            if (arrayMatch != null) {
                val messagesContent = arrayMatch.groupValues[1]
                // Extraer todos los mensajes del array
                val messagePattern = "\"([^\"]+)\"".toRegex()
                val messages = messagePattern.findAll(messagesContent).map { it.groupValues[1] }.toList()
                
                if (messages.isNotEmpty()) {
                    // Retornar el primer mensaje o combinarlos si hay varios
                    if (messages.size == 1) {
                        messages[0]
                    } else {
                        messages.joinToString(", ")
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            RepositoryLogger.e("AlbumRepository", "Error extracting nested validation messages", e)
            null
        }
    }
}
