package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Collector
import com.example.vinylsapp.data.network.CollectorApiService
import com.example.vinylsapp.data.network.CollectorDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Pattern: CollectorRepository
 * Expone una fuente de datos remota para la pantalla de coleccionistas.
 */
@Singleton
class CollectorRepository @Inject constructor(
    private val apiService: CollectorApiService
) {

    /**
     * Retorna un flujo que obtiene los coleccionistas desde el API remoto.
     */
    fun getCollectors(): Flow<Result<List<Collector>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCollectors()
            if (response.isSuccessful) {
                val collectors = response.body().orEmpty().map { it.toDomain() }
                emit(Result.Success(collectors))
            } else {
                emit(
                    Result.Error(
                        exception = Exception("Error ${response.code()}"),
                        message = "Error al cargar los coleccionistas: ${response.message()}"
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                Result.Error(
                    exception = e,
                    message = "Error de conexión: ${e.localizedMessage ?: "desconocido"}"
                )
            )
        }
    }.flowOn(Dispatchers.IO)
}

private fun CollectorDto.toDomain(): Collector {
    val albumCount = collectorAlbums?.size ?: 0
    val avatar = favoritePerformers?.firstOrNull()?.image.orEmpty()
    val summary = comments?.firstOrNull()?.description.orEmpty()

    return Collector(
        id = id,
        name = name,
        avatarUrl = avatar,
        country = "",
        city = "",
        shortBio = summary,
        totalAlbums = albumCount,
        telephone = telephone.orEmpty(),
        email = email.orEmpty()
    )
}