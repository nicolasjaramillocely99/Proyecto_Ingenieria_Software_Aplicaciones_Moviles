package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Musician
import com.example.vinylsapp.data.network.MusicianApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Pattern: MusicianRepository
 * Actúa como única fuente de verdad para los datos de músicos
 * Abstrae la lógica de obtención de datos del ViewModel
 */
@Singleton
class MusicianRepository @Inject constructor(
    private val apiService: MusicianApiService
) {
    
    /**
     * Obtiene la lista de músicos desde el API
     * Retorna un Flow para permitir observación reactiva de los datos
     * 
     * @return Flow con Result que puede ser Success, Error o Loading
     */
    fun getMusicians(): Flow<Result<List<Musician>>> = flow {
        try {
            // Emitir estado de carga
            emit(Result.Loading)
            
            // Hacer la petición al API
            val response = apiService.getMusicians()
            
            if (response.isSuccessful && response.body() != null) {
                // Éxito: emitir los datos
                emit(Result.Success(response.body()!!))
            } else {
                // Error en la respuesta
                emit(Result.Error(
                    exception = Exception("Error ${response.code()}"),
                    message = "Error al cargar los músicos: ${response.message()}"
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
     * Obtiene un músico específico por su ID
     * 
     * @param musicianId ID del músico a obtener
     * @return Flow con Result que contiene el músico o un error
     */
    fun getMusicianById(musicianId: Int): Flow<Result<Musician>> = flow {
        try {
            emit(Result.Loading)
            
            val response = apiService.getMusicianById(musicianId)
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error(
                    exception = Exception("Error ${response.code()}"),
                    message = "Error al cargar el músico"
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
