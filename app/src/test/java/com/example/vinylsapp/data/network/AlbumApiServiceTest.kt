package com.example.vinylsapp.data.network

import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.CreateAlbumRequest
import com.example.vinylsapp.data.model.Performer
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
 * Pruebas de integración para AlbumApiService usando MockWebServer
 * 
 * Objetivo: Verificar que el servicio de API maneja correctamente:
 * - Respuestas JSON válidas
 * - Deserialización de datos
 * - Códigos de estado HTTP
 * - Timeouts y errores de red
 * 
 * MockWebServer simula un servidor HTTP real sin necesidad del backend
 */
class AlbumApiServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: AlbumApiService
    private val gson = Gson()
    
    // Datos de prueba
    private val testAlbums = listOf(
        Album(
            id = 1,
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
            releaseDate = "1984-08-01",
            description = "Buscando América es el tercer álbum de estudio",
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
            description = "A Night at the Opera es el cuarto álbum",
            genre = "Rock",
            recordLabel = "EMI",
            performers = null
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
            .create(AlbumApiService::class.java)
    }
    
    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
    
    /**
     * Test: Verificar que getAlbums parsea correctamente una respuesta exitosa
     */
    @Test
    fun `getAlbums returns albums when server responds with 200`() = runTest {
        // Given: El servidor mock retorna una lista de álbumes
        val jsonResponse = gson.toJson(testAlbums)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: La respuesta debe ser exitosa con los datos correctos
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertEquals("Should return 2 albums", 2, response.body()!!.size)
        assertEquals("First album name should match", "Buscando América", response.body()!![0].name)
        assertEquals("First album genre should match", "Salsa", response.body()!![0].genre)
    }
    
    /**
     * Test: Verificar que getAlbums maneja correctamente una lista vacía
     */
    @Test
    fun `getAlbums returns empty list when server returns empty array`() = runTest {
        // Given: El servidor retorna un array vacío
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: Debe retornar una lista vacía
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertTrue("Should return empty list", response.body()!!.isEmpty())
    }
    
    /**
     * Test: Verificar que getAlbums maneja código de error 404
     */
    @Test
    fun `getAlbums returns error response when server responds with 404`() = runTest {
        // Given: El servidor retorna 404
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: La respuesta debe indicar error
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }
    
    /**
     * Test: Verificar que getAlbums maneja código de error 500
     */
    @Test
    fun `getAlbums returns error response when server responds with 500`() = runTest {
        // Given: El servidor retorna 500
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: La respuesta debe indicar error
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 500", 500, response.code())
    }
    
    /**
     * Test: Verificar que getAlbumById funciona correctamente
     */
    @Test
    fun `getAlbumById returns single album when server responds correctly`() = runTest {
        // Given: El servidor retorna un álbum específico
        val album = testAlbums[0]
        val jsonResponse = gson.toJson(album)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getAlbumById
        val response = apiService.getAlbumById(1)
        
        // Then: Debe retornar el álbum correcto
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertEquals("Album ID should match", 1, response.body()!!.id)
        assertEquals("Album name should match", "Buscando América", response.body()!!.name)
    }
    
    /**
     * Test: Verificar que getAlbumById maneja álbum no encontrado
     */
    @Test
    fun `getAlbumById returns 404 when album does not exist`() = runTest {
        // Given: El servidor retorna 404 para un álbum inexistente
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Album not found")
        )
        
        // When: Llamamos a getAlbumById con ID inexistente
        val response = apiService.getAlbumById(999)
        
        // Then: Debe retornar error 404
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }
    
    /**
     * Test: Verificar que se parsean correctamente los performers
     */
    @Test
    fun `getAlbums correctly parses performers data`() = runTest {
        // Given: El servidor retorna álbumes con performers
        val jsonResponse = gson.toJson(testAlbums)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: Los performers deben estar correctamente parseados
        val albumWithPerformers = response.body()!![0]
        assertNotNull("Performers should not be null", albumWithPerformers.performers)
        assertEquals("Should have 1 performer", 1, albumWithPerformers.performers!!.size)
        assertEquals("Performer name should match", "Rubén Blades", albumWithPerformers.performers!![0].name)
    }
    
    /**
     * Test: Verificar que se manejan álbumes sin performers
     */
    @Test
    fun `getAlbums handles albums without performers`() = runTest {
        // Given: El servidor retorna álbumes sin performers
        val jsonResponse = gson.toJson(testAlbums)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a getAlbums
        val response = apiService.getAlbums()
        
        // Then: Los álbumes sin performers deben tener performers = null
        val albumWithoutPerformers = response.body()!![1]
        assertNull("Performers should be null", albumWithoutPerformers.performers)
    }
    
    /**
     * Test: Verificar que se envía la petición correcta
     */
    @Test
    fun `getAlbums sends correct HTTP request`() = runTest {
        // Given: Configurar respuesta del servidor
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )
        
        // When: Llamamos a getAlbums
        apiService.getAlbums()
        
        // Then: Verificar que la petición es correcta
        val request = mockWebServer.takeRequest()
        assertEquals("Should use GET method", "GET", request.method)
        assertEquals("Should request /albums endpoint", "/albums", request.path)
    }
    
    /**
     * Test: Verificar que se envía la petición correcta para getAlbumById
     */
    @Test
    fun `getAlbumById sends correct HTTP request`() = runTest {
        // Given: Configurar respuesta del servidor
        val album = testAlbums[0]
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(album))
        )
        
        // When: Llamamos a getAlbumById
        apiService.getAlbumById(1)
        
        // Then: Verificar que la petición es correcta
        val request = mockWebServer.takeRequest()
        assertEquals("Should use GET method", "GET", request.method)
        assertEquals("Should request /albums/1 endpoint", "/albums/1", request.path)
    }
    
    /**
     * Test: Verificar que createAlbum envía POST con el body correcto y parsea la respuesta
     */
    @Test
    fun `createAlbum sends POST request and returns created album`() = runTest {
        // Given: Request de creación de álbum
        val createRequest = CreateAlbumRequest(
            name = "Test Album",
            cover = "https://example.com/cover.jpg",
            releaseDate = "2024-01-15",
            description = "Test description",
            genre = "Rock",
            recordLabel = "Sony Music"
        )
        
        val createdAlbum = Album(
            id = 1,
            name = "Test Album",
            cover = "https://example.com/cover.jpg",
            releaseDate = "2024-01-15",
            description = "Test description",
            genre = "Rock",
            recordLabel = "Sony Music",
            performers = null
        )
        
        // El servidor retorna el álbum creado
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(gson.toJson(createdAlbum))
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a createAlbum
        val response = apiService.createAlbum(createRequest)
        
        // Then: La respuesta debe ser exitosa con el álbum creado
        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())
        assertEquals("Album ID should match", 1, response.body()!!.id)
        assertEquals("Album name should match", "Test Album", response.body()!!.name)
        assertEquals("Album genre should match", "Rock", response.body()!!.genre)
        
        // Verificar que la petición fue POST
        val request = mockWebServer.takeRequest()
        assertEquals("Should use POST method", "POST", request.method)
        assertEquals("Should request /albums endpoint", "/albums", request.path)
        assertEquals("Should have JSON content type", 
            "application/json; charset=UTF-8", 
            request.getHeader("Content-Type"))
    }
    
    /**
     * Test: Verificar que createAlbum maneja errores de validación (400)
     */
    @Test
    fun `createAlbum returns error when validation fails`() = runTest {
        // Given: Request inválido (URL malformada)
        val invalidRequest = CreateAlbumRequest(
            name = "Test Album",
            cover = "invalid-url",  // URL inválida
            releaseDate = "2024-01-15",
            description = "Test description",
            genre = "Rock",
            recordLabel = "Sony Music"
        )
        
        // El servidor retorna error 400
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("""{"message": ["cover must be a URL"]}""")
                .addHeader("Content-Type", "application/json")
        )
        
        // When: Llamamos a createAlbum
        val response = apiService.createAlbum(invalidRequest)
        
        // Then: La respuesta debe indicar error
        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 400", 400, response.code())
    }
}
