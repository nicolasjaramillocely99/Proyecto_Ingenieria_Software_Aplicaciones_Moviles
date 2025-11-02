package com.example.vinylsapp.data.repository

import com.example.vinylsapp.data.model.Musician
import com.example.vinylsapp.data.network.MusicianApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas unitarias para MusicianRepository
 * 
 * Objetivo: Verificar que el repository maneja correctamente:
 * - Respuestas exitosas del API
 * - Errores de red
 * - Respuestas con códigos de error HTTP
 * - Transformación de datos
 */
class ArtistRepositoryTest {
    
    // System Under Test
    private lateinit var repository: MusicianRepository
    
    // Dependencias mockeadas
    private lateinit var apiService: MusicianApiService
    
    // Datos de prueba
    private val testArtist = listOf(
        Musician(
            id = 100,
            name = "Rubén Blades Bellido de Luna",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
            description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
            birthDate = "1948-07-16T00:00:00.000Z"
        ),
        Musician(
            id = 2,
            name = "Celia Cruz",
            image = "https://upload.wikimedia.org/wikipedia/commons/d/d8/Celia_Cruz%2C_1957_%28cropped%29.jpg",
            description = "Fue una cantante cubana de música tropical. Apodada «la Reina de la salsa»​ y «la Guarachera de Cuba»,​ es ampliamente considerada como una de las artistas latinas más populares e importantes del siglo XX y un icono de la música latina.",
            birthDate = "1948-07-16T05:00:00.000Z"
        ),
        Musician(
            id = 5,
            name = "Shakira Isabel Mebarak Ripoll",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/2023-11-16_Gala_de_los_Latin_Grammy%2C_03_%28cropped%2901.jpg/330px-2023-11-16_Gala_de_los_Latin_Grammy%2C_03_%28cropped%2901.jpg",
            description = "Es una cantante, compositora, productora, bailarina, multinstrumentista, empresaria y actriz colombiana.",
            birthDate = "1948-07-16T05:00:00.000Z"
        )
    )
    
    @Before
    fun setup() {
        // Crear mock del API service
        apiService = mockk()
        
        // Crear instancia del repository con el mock
        repository = MusicianRepository(apiService)
    }
    
    /**
     * Test: Verificar que el repository emite Loading y luego Success con datos
     * cuando el API responde correctamente
     */
    @Test
    fun `getMusicians emits Loading then Success when API call is successful`() = runTest {
        // Given: API retorna una respuesta exitosa
        val successResponse = Response.success(testArtist)
        coEvery { apiService.getMusicians() } returns successResponse
        
        // When: Llamamos a getMusicians
        val flow = repository.getMusicians()
        
        // Then: El primer valor debe ser Loading
        val firstEmission = flow.first()
        assertTrue("First emission should be Loading", firstEmission is Result.Loading)
    }
    
    /**
     * Test: Verificar que se retornan los datos correctos después de Loading
     */
    @Test
    fun `getMusicians returns correct data after loading`() = runTest {
        // Given: API retorna una respuesta exitosa
        val successResponse = Response.success(testArtist)
        coEvery { apiService.getMusicians() } returns successResponse
        
        // When: Recolectamos todos los valores del flow
        val emissions = mutableListOf<Result<List<Musician>>>()
        repository.getMusicians().collect { emissions.add(it) }
        
        // Then: Debe haber 2 emisiones (Loading y Success)
        assertEquals("Should emit Loading and Success", 3, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Success", emissions[1] is Result.Success)
        
        // Verificar que los datos son correctos
        val successResult = emissions[1] as Result.Success
        assertEquals("Should return 3 musicians", 3, successResult.data.size)
        assertEquals("First musician name should match", "Rubén Blades Bellido de Luna", successResult.data[0].name)
    }
    
    /**
     * Test: Verificar que se maneja correctamente un error HTTP (404, 500, etc.)
     */
    @Test
    fun `getMusicians emits Error when API returns error code`() = runTest {
        // Given: API retorna un código de error 404
        val errorResponse = Response.error<List<Musician>>(
            404,
            "Not Found".toResponseBody(null)
        )
        coEvery { apiService.getMusicians() } returns errorResponse
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Musician>>>()
        repository.getMusicians().collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(3, emissions.size)
        assertTrue("First should be Loading", emissions[0] is Result.Loading)
        assertTrue("Second should be Error", emissions[1] is Result.Error)
        
        val errorResult = emissions[1] as Result.Error
        assertTrue("Error message should contain status code",
            errorResult.message?.contains("Error 404") == true || errorResult.message?.contains("músicos") == true)
    }
    
    /**
     * Test: Verificar que se maneja correctamente una excepción de red
     */
    @Test
    fun `getMusicians emits Error when network exception occurs`() = runTest {
        // Given: API lanza una excepción de red
        val networkException = IOException("Network unavailable")
        coEvery { apiService.getMusicians() } throws networkException
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Musician>>>()
        repository.getMusicians().collect { emissions.add(it) }
        
        // Then: Debe emitir Loading y Error
        assertEquals(3, emissions.size)
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
    fun `getMusicians returns empty list when API returns empty array`() = runTest {
        // Given: API retorna una lista vacía
        val emptyResponse = Response.success(emptyList<Musician>())
        coEvery { apiService.getMusicians() } returns emptyResponse
        
        // When: Recolectamos los valores
        val emissions = mutableListOf<Result<List<Musician>>>()
        repository.getMusicians().collect { emissions.add(it) }
        
        // Then: Debe retornar Success con lista vacía
        val successResult = emissions[1] as Result.Success
        assertTrue("Should return empty list", successResult.data.isEmpty())
    }
    
    /**
     * Test: Verificar que getMusicianById funciona correctamente
     */
    @Test
    fun `getMusicianById returns correct musician when API call is successful`() = runTest {
        // Given: API retorna un álbum específico
        val artist = testArtist[0]
        val successResponse = Response.success(artist)
        coEvery { apiService.getMusicianById(1) } returns successResponse
        
        // When: Obtenemos el álbum
        val emissions = mutableListOf<Result<Musician>>()
        repository.getMusicianById(1).collect { emissions.add(it) }
        
        // Then: Debe retornar el músico correcto
        assertEquals(3, emissions.size)
        assertTrue("Second should be Success", emissions[1] is Result.Success)
        
        val successResult = emissions[1] as Result.Success
        assertEquals("Should return musician with id 100", 100, successResult.data.id)
        assertEquals("Artist name should match", "Rubén Blades Bellido de Luna", successResult.data.name)
    }
    
    /**
     * Test: Verificar manejo de errores en getMusicianById
     */
    @Test
    fun `getMusicianById emits Error when artist not found`() = runTest {
        // Given: API retorna 404
        val errorResponse = Response.error<Musician>(
            404,
            "Musician not found".toResponseBody(null)
        )
        coEvery { apiService.getMusicianById(999) } returns errorResponse
        
        // When: Intentamos obtener un álbum inexistente
        val emissions = mutableListOf<Result<Musician>>()
        repository.getMusicianById(999).collect { emissions.add(it) }
        
        // Then: Debe emitir Error
        assertTrue("Should emit Error", emissions[1] is Result.Error)
    }
}
