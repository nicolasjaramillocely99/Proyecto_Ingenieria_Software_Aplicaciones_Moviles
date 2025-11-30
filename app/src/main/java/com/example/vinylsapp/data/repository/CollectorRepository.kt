package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Collector
import com.example.vinylsapp.data.model.FeaturedAlbum
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

    /**
     * Obtiene el detalle de un coleccionista combinando la respuesta del API con datos enriquecidos.
     * Si el endpoint no devuelve resultados, se usan datos hardcodeados y se notifica cuando el ID no existe.
     */
    fun getCollectorDetail(collectorId: Int): Flow<Result<Collector>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCollectorDetail(collectorId)
            val collectorDto = when {
                response.isSuccessful -> response.body()
                response.code() == 404 -> null
                else -> throw Exception("Error ${response.code()} - ${response.message()}")
            }

            val extras = collectorExtras[collectorId]
            when {
                collectorDto != null -> emit(Result.Success(collectorDto.toDomain(extras)))
                extras != null -> emit(Result.Success(extras.toCollector(collectorId)))
                else -> emit(
                    Result.Error(
                        exception = IllegalArgumentException("Collector not found"),
                        message = "El coleccionista con id $collectorId no existe"
                    )
                )
            }
        } catch (e: Exception) {
            val extras = collectorExtras[collectorId]
            if (extras != null) {
                emit(Result.Success(extras.toCollector(collectorId)))
            } else {
                emit(
                    Result.Error(
                        exception = e,
                        message = "Error de conexión: ${e.localizedMessage ?: "desconocido"}"
                    )
                )
            }
        }
    }.flowOn(Dispatchers.IO)
}

private fun CollectorDto.toDomain(extras: CollectorExtras? = null): Collector {
    val albumCount = collectorAlbums?.size ?: 0
    val avatar = favoritePerformers?.firstOrNull()?.image.orEmpty()
    val summary = comments?.firstOrNull()?.description.orEmpty()

    val featuredAlbums = extras?.featuredAlbums ?: collectorAlbums.orEmpty().map {
        FeaturedAlbum(
            id = it.id,
            title = "Álbum #${it.id}",
            artist = favoritePerformers?.firstOrNull()?.name.orEmpty(),
            coverUrl = favoritePerformers?.firstOrNull()?.image.orEmpty()
        )
    }

    return Collector(
        id = id,
        name = name,
        avatarUrl = extras?.avatarUrl ?: avatar,
        country = extras?.country.orEmpty(),
        city = extras?.city.orEmpty(),
        shortBio = extras?.bio ?: summary,
        totalAlbums = extras?.totalAlbums ?: albumCount,
        telephone = telephone.orEmpty(),
        email = email.orEmpty(),
        favoriteGenres = extras?.favoriteGenres ?: emptyList(),
        favoriteArtists = extras?.favoriteArtists
            ?: favoritePerformers?.mapNotNull { it.name }.orEmpty(),
        featuredAlbums = featuredAlbums
    )
}

private val collectorExtras: Map<Int, CollectorExtras> = mapOf(
    1 to CollectorExtras(
        name = "Julian David Rodriguez",
        avatarUrl = "https://i.imgur.com/4uNQpRq.jpeg",
        city = "Bogotá",
        country = "Colombia",
        bio = "Apasionado por el rock progresivo y coleccionista desde hace 10 años.",
        totalAlbums = 24,
        favoriteGenres = listOf("Rock Progresivo", "Jazz", "Indie"),
        favoriteArtists = listOf("Pink Floyd", "Porcupine Tree", "Hiatus Kaiyote"),
        featuredAlbums = listOf(
            FeaturedAlbum(
                id = 120,
                title = "The Dark Side of the Moon",
                artist = "Pink Floyd",
                coverUrl = "https://i.imgur.com/7b1KxVI.jpeg"
            ),
            FeaturedAlbum(
                id = 121,
                title = "In Absentia",
                artist = "Porcupine Tree",
                coverUrl = "https://i.imgur.com/ZAXE8oX.jpeg"
            ),
            FeaturedAlbum(
                id = 122,
                title = "Mood Valiant",
                artist = "Hiatus Kaiyote",
                coverUrl = "https://i.imgur.com/e6H3UwP.jpeg"
            )
        )
    ),
    2 to CollectorExtras(
        name = "Andrea Pérez",
        avatarUrl = "https://i.imgur.com/o7pXBtb.jpeg",
        city = "Medellín",
        country = "Colombia",
        bio = "Coleccionista de vinilos latinos y tropicales.",
        totalAlbums = 12,
        favoriteGenres = listOf("Salsa", "Bossa Nova", "Cumbia"),
        favoriteArtists = listOf("Rubén Blades", "João Gilberto"),
        featuredAlbums = listOf(
            FeaturedAlbum(
                id = 210,
                title = "Siembra",
                artist = "Willie Colón & Rubén Blades",
                coverUrl = "https://i.imgur.com/6oI5S3z.jpeg"
            )
        )
    )
)

private data class CollectorExtras(
    val name: String,
    val avatarUrl: String,
    val city: String,
    val country: String,
    val bio: String,
    val totalAlbums: Int,
    val favoriteGenres: List<String>,
    val favoriteArtists: List<String>,
    val featuredAlbums: List<FeaturedAlbum>
) {
    fun toCollector(id: Int): Collector {
        return Collector(
            id = id,
            name = name,
            avatarUrl = avatarUrl,
            country = country,
            city = city,
            shortBio = bio,
            totalAlbums = totalAlbums,
            telephone = "",
            email = "",
            favoriteGenres = favoriteGenres,
            favoriteArtists = favoriteArtists,
            featuredAlbums = featuredAlbums
        )
    }
}
