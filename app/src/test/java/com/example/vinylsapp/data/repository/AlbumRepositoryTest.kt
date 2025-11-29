package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.CreateTrackRequest
import com.example.vinylsapp.data.model.Performer
import com.example.vinylsapp.data.model.Track
import com.example.vinylsapp.data.network.AlbumApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Pruebas unitarias para AlbumRepository
 * 
 * Objetivo: Verificar que el repository maneja correctamente:
 * - Respuestas exitosas del API
 * - Errores de red
 * - Respuestas con códigos de error HTTP
 * - Transformación de datos
 */
class AlbumRepositoryTest {
    
    // System Under Test
    private lateinit var repository: AlbumRepository
    
    // Dependencias mockeadas
    private lateinit var apiService: AlbumApiService
    
    // Datos de prueba
    private val testAlbums = listOf(
        Album(
            id = 1,
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
            releaseDate = "1984-08-01",
            description = "Buscando América es el tercer álbum de estudio de Rubén Blades y Willie Colón",
            genre = "Salsa",
            recordLabel = "Elektra",
            performers = listOf(
                Performer(
                    id = 1,
                    name = "Rubén Blades",
                    image = "https://example.com/ruben.jpg",
                    description = "Cantante panameño"
                )
            )
        ),
        Album(
            id = 2,
            name = "A Night at the Opera",
            cover = "https://i.pinimg.com/564x/02/f2/5e/02f25e5e1b9c21a6788998dce3bd8f74.jpg",
            releaseDate = "1975-11-21",
            description = "A Night at the Opera es el cuarto álbum de estudio de Queen",
            genre = "Rock",
            recordLabel = "EMI",
            performers = listOf(
                Performer(
                    id = 2,
                    name = "Queen",
                    image = "https://example.com/queen.jpg",
                    description = "Banda británica de rock"
                )
            )
        )
    )
    
    @Before
    fun setup() {
        // Crear mock del API service
        apiService = mockk()
        
        // Crear instancia del repository con el mock
        repository = AlbumRepository(apiService)
    }
    
    /**
     * Test: Verificar que el repository emite Loading y luego Success con datos
     * cuando el API responde correctamente
     */
    @Test
    fun `getAlbums emits Loading then Success when API call is successful`() = runTest {
        // Given: API retorna una respuesta exitosa
        val successResponse = Response.success(testAlbums)
        coEvery { apiService.getAlbums() } returns successResponse
        
        // When: Llamamos a getAlbums
        val flow = repository.getAlbums()
        
        // Then: El primer valor debe ser Loading
        val firstEmission = flow.first()
        assertTrue("First emission should be Loading", firstEmission is Result.Loading)
    }
    
    /**
     * Test: Verificar que se retornan los datos correctos después de Loading
     */
    @Test
    fun `getAlbums returns correct data after loading`() = runTest {
        // Given: API retorna una respuesta exitosa
        val successResponse = Response.success(testAlbums)
        coEvery { apiService.getAlbums() } returns successResponse
        
        // When: Recolectamos todos los valores del flow
        val emissions = mutableListOf<Result<List<Album>>>()
        repository.getAlbums().collect { emissions.add(it) }
        
        // Then: Debe haber 2 emisiones (Loading y Success)
        assertEquals("Should emit Loading and Success", 2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Success", emissions[1] is Result.Success)
        
        // Verificar que los datos son correctos
        val successResult = emissions[1] as Result.Success
        assertEquals("Should return 2 albums", 2, successResult.data.size)
        assertEquals("First album name should match", "Buscando América", successResult.data[0].name)
    }
    
    /**
     * Test: Verificar que se maneja correctamente un error HTTP (404, 500, etc.)
     */
    @Test
    fun `getAlbums emits Error when API returns error code`() = runTest {
        // Given: API retorna un código de error 404
        val errorResponse = Response.error<List<Album>>(
            404,
            "Not Found".toResponseBody(null)
        )
        coEvery { apiService.getAlbums() } returns errorResponse
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Album>>>()
        repository.getAlbums().collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Error", emissions[1] is Result.Error)
        
        val errorResult = emissions[1] as Result.Error
        assertTrue("Error message should contain status code", 
            errorResult.message?.contains("Error 404") == true || errorResult.message?.contains("álbumes") == true)
    }
    
    /**
     * Test: Verificar que se maneja correctamente una excepción de red
     */
    @Test
    fun `getAlbums emits Error when network exception occurs`() = runTest {
        // Given: API lanza una excepción de red
        val networkException = java.io.IOException("Network unavailable")
        coEvery { apiService.getAlbums() } throws networkException
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Album>>>()
        repository.getAlbums().collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Error", emissions[1] is Result.Error)
        
        val errorResult = emissions[1] as Result.Error
        assertNotNull("Error should have an exception", errorResult.exception)
        assertTrue("Error message should mention connection", 
            errorResult.message?.contains("conexión", ignoreCase = true) == true)
    }
    
    /**
     * Test: Verificar que se maneja una lista vacía correctamente
     */
    @Test
    fun `getAlbums returns empty list when API returns empty array`() = runTest {
        // Given: API retorna una lista vacía
        val emptyResponse = Response.success(emptyList<Album>())
        coEvery { apiService.getAlbums() } returns emptyResponse
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Album>>>()
        repository.getAlbums().collect { emissions.add(it) }
        
        // Then: Debe retornar Success con lista vacía
        val successResult = emissions[1] as Result.Success
        assertTrue("Should return empty list", successResult.data.isEmpty())
    }
    
    /**
     * Test: Verificar que getAlbumById funciona correctamente
     */
    @Test
    fun `getAlbumById returns correct album when API call is successful`() = runTest {
        // Given: API retorna un álbum específico
        val album = testAlbums[0]
        val successResponse = Response.success(album)
        coEvery { apiService.getAlbumById(1) } returns successResponse
        
        // When: Obtenemos el álbum
        val emissions = mutableListOf<Result<Album>>()
        repository.getAlbumById(1).collect { emissions.add(it) }
        
        // Then: Debe retornar el álbum correcto
        assertEquals(2, emissions.size)
        assertTrue("Second should be Success", emissions[1] is Result.Success)
        
        val successResult = emissions[1] as Result.Success
        assertEquals("Should return album with id 1", 1, successResult.data.id)
        assertEquals("Album name should match", "Buscando América", successResult.data.name)
    }
    
    /**
     * Test: Verificar manejo de errores en getAlbumById
     */
    @Test
    fun `getAlbumById emits Error when album not found`() = runTest {
        // Given: API retorna 404
        val errorResponse = Response.error<Album>(
            404,
            "Album not found".toResponseBody(null)
        )
        coEvery { apiService.getAlbumById(999) } returns errorResponse
        
        // When: Intentamos obtener un álbum inexistente
        val emissions = mutableListOf<Result<Album>>()
        repository.getAlbumById(999).collect { emissions.add(it) }
        
        // Then: Debe emitir Error
        assertTrue("Should emit Error", emissions[1] is Result.Error)
    }
    
    /**
     * Test: Verificar que createTrack emite Loading y luego Success cuando es exitoso
     */
    @Test
    fun `createTrack emits Loading then Success when API call is successful`() = runTest {
        // Given: API retorna una respuesta exitosa
        val albumId = 1
        val trackRequest = CreateTrackRequest(
            name = "Test Track",
            duration = "03:45",
            albumId = albumId,
            seconds = 225,
            number = 1,
            composer = "Test Composer"
        )
        val createdTrack = Track(
            id = 1,
            name = "Test Track",
            duration = "03:45",
            albumId = albumId,
            seconds = 225,
            number = 1,
            composer = "Test Composer"
        )
        val successResponse = Response.success(createdTrack)
        coEvery { apiService.createTrack(albumId, trackRequest) } returns successResponse
        
        // When: Creamos el track
        val flow = repository.createTrack(albumId, trackRequest)
        
        // Then: El primer valor debe ser Loading
        val firstEmission = flow.first()
        assertTrue("First emission should be Loading", firstEmission is Result.Loading)
    }
    
    /**
     * Test: Verificar que createTrack retorna el track creado correctamente
     */
    @Test
    fun `createTrack returns created track after loading`() = runTest {
        // Given: API retorna una respuesta exitosa
        val albumId = 1
        val trackRequest = CreateTrackRequest(
            name = "Test Track",
            duration = "03:45",
            albumId = albumId
        )
        val createdTrack = Track(
            id = 1,
            name = "Test Track",
            duration = "03:45",
            albumId = albumId
        )
        val successResponse = Response.success(createdTrack)
        coEvery { apiService.createTrack(albumId, trackRequest) } returns successResponse
        
        // When: Recolectamos todos los valores del flow
        val emissions = mutableListOf<Result<Track>>()
        repository.createTrack(albumId, trackRequest).collect { emissions.add(it) }
        
        // Then: Debe haber 2 emisiones (Loading y Success)
        assertEquals("Should emit Loading and Success", 2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Success", emissions[1] is Result.Success)
        
        // Verificar que los datos son correctos
        val successResult = emissions[1] as Result.Success
        assertEquals("Should return track with id 1", 1, successResult.data.id)
        assertEquals("Track name should match", "Test Track", successResult.data.name)
        assertEquals("Track albumId should match", albumId, successResult.data.albumId)
    }
    
    /**
     * Test: Verificar que createTrack maneja errores HTTP correctamente
     */
    @Test
    fun `createTrack emits Error when API returns error code`() = runTest {
        // Given: API retorna un código de error 400
        val albumId = 1
        val trackRequest = CreateTrackRequest(
            name = "Test Track",
            albumId = albumId
        )
        val errorResponse = Response.error<Track>(
            400,
            "Bad Request".toResponseBody(null)
        )
        coEvery { apiService.createTrack(albumId, trackRequest) } returns errorResponse
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<Track>>()
        repository.createTrack(albumId, trackRequest).collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Error", emissions[1] is Result.Error)
    }
    
    /**
     * Test: Verificar que createTrack maneja excepciones de red correctamente
     */
    @Test
    fun `createTrack emits Error when network exception occurs`() = runTest {
        // Given: API lanza una excepción de red
        val albumId = 1
        val trackRequest = CreateTrackRequest(
            name = "Test Track",
            albumId = albumId
        )
        val networkException = java.io.IOException("Network unavailable")
        coEvery { apiService.createTrack(albumId, trackRequest) } throws networkException
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<Track>>()
        repository.createTrack(albumId, trackRequest).collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(2, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Error", emissions[1] is Result.Error)
        
        val errorResult = emissions[1] as Result.Error
        assertNotNull("Error should have an exception", errorResult.exception)
        assertTrue("Error message should mention connection", 
            errorResult.message?.contains("conexión", ignoreCase = true) == true)
    }
}
