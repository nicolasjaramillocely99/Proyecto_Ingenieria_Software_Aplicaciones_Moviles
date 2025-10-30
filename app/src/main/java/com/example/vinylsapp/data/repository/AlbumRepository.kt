package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.network.AlbumApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

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
}
