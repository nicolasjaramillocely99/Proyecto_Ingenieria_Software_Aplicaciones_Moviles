package com.example.vinylsapp.data.network

import com.example.vinylsapp.data.model.Musician
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Pruebas de integración para ArtistApiService usando MockWebServer
 * 
 * Objetivo: Verificar que el servicio de API maneja correctamente:
 * - Respuestas JSON válidas
 * - Deserialización de datos
 * - Códigos de estado HTTP
 * - Timeouts y errores de red
 * 
 * MockWebServer simula un servidor HTTP real sin necesidad del backend
 */
class ArtistApiServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: MusicianApiService
    private val gson = Gson()
    
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
        // Inicializar el servidor mock
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // Crear el servicio de API apuntando al servidor mock
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
        
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicianApiService::class.java)
    }
    
    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
    
    /**
     * Test: Verificar que getMusicians parsea correctamente una respuesta exitosa
     */
    @Test
    fun `getMusicians returns artists when server responds with 200`() = runTest {
        // Given: El servidor mock retorna una lista de álbumes
        val jsonResponse = gson.toJson(testArtist)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getMusicians
        val response = apiService.getMusicians()
        
        // Then: La respuesta debe ser exitosa con los datos correctos
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertEquals("Should return 3 artists", 3, response.body()!!.size)
        assertEquals("First artist name should match", "Rubén Blades Bellido de Luna", response.body()!![0].name)
        assertEquals("First artist birthDate should match", "1948-07-16T00:00:00.000Z", response.body()!![0].birthDate)
    }
    
    /**
     * Test: Verificar que getMusicians maneja correctamente una lista vacía
     */
    @Test
    fun `getMusicians returns empty list when server returns empty array`() = runTest {
        // Given: El servidor retorna un array vacío
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getMusicians
        val response = apiService.getMusicians()
        
        // Then: Debe retornar una lista vacía
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertTrue("Should return empty list", response.body()!!.isEmpty())
    }
    
    /**
     * Test: Verificar que getMusicians maneja código de error 404
     */
    @Test
    fun `getMusicians returns error response when server responds with 404`() = runTest {
        // Given: El servidor retorna 404
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )
        
        // When: Llamamos a getMusicians
        val response = apiService.getMusicians()
        
        // Then: La respuesta debe indicar error
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }
    
    /**
     * Test: Verificar que getMusicians maneja código de error 500
     */
    @Test
    fun `getMusicians returns error response when server responds with 500`() = runTest {
        // Given: El servidor retorna 500
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )
        
        // When: Llamamos a getMusicians
        val response = apiService.getMusicians()
        
        // Then: La respuesta debe indicar error
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 500", 500, response.code())
    }
    
    /**
     * Test: Verificar que getMusicianById funciona correctamente
     */
    @Test
    fun `getMusicianById returns single artist when server responds correctly`() = runTest {
        // Given: El servidor retorna un álbum específico
        val artist = testArtist[0]
        val jsonResponse = gson.toJson(artist)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getMusicianById
        val response = apiService.getMusicianById(1)
        
        // Then: Debe retornar el artista correcto
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertEquals("Artist ID should match", 100, response.body()!!.id)
        assertEquals("Artist name should match", "Rubén Blades Bellido de Luna", response.body()!!.name)
    }
    
    /**
     * Test: Verificar que getMusicianById maneja artista no encontrado
     */
    @Test
    fun `getMusicianById returns 404 when artist does not exist`() = runTest {
        // Given: El servidor retorna 404 para un artista inexistente
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("The musician with the given id was not found")
        )
        
        // When: Llamamos a getMusicianById con ID inexistente
        val response = apiService.getMusicianById(999)
        
        // Then: Debe retornar error 404
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }


    
    /**
     * Test: Verificar que se envía la petición correcta
     */
    @Test
    fun `getMusicians sends correct HTTP request`() = runTest {
        // Given: Configurar respuesta del servidor
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )
        
        // When: Llamamos a getMusicians
        apiService.getMusicians()
        
        // Then: Verificar que la petición es correcta
        val request = mockWebServer.takeRequest()
        assertEquals("Should use GET method", "GET", request.method)
        assertEquals("Should request /musicians endpoint", "/musicians", request.path)
    }
    
    /**
     * Test: Verificar que se envía la petición correcta para getMusicianById
     */
    @Test
    fun `getMusicianById sends correct HTTP request`() = runTest {
        // Given: Configurar respuesta del servidor
        val artist = testArtist[0]
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(artist))
        )
        
        // When: Llamamos a getMusicianById
        apiService.getMusicianById(100)
        
        // Then: Verificar que la petición es correcta
        val request = mockWebServer.takeRequest()
        assertEquals("Should use GET method", "GET", request.method)
        assertEquals("Should request /musicians/100 endpoint", "/musicians/100", request.path)
    }
}
