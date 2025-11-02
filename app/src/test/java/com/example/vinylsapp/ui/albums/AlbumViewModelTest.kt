package com.example.vinylsapp.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.Performer
import com.example.vinylsapp.data.repository.AlbumRepository
import com.example.vinylsapp.data.repository.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias para AlbumViewModel
 * 
 * Objetivo: Verificar que el ViewModel:
 * - Carga los álbumes al iniciar
 * - Actualiza el estado de la UI correctamente según las respuestas
 * - Maneja estados de Loading, Success y Error
 * - Permite reintentar la carga
 * - Limpia errores correctamente
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest {
    
    // Regla para ejecutar tareas de forma síncrona en tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Dispatcher de prueba para controlar las coroutines
    private val testDispatcher = StandardTestDispatcher()
    
    // System Under Test
    private lateinit var viewModel: AlbumViewModel
    
    // Dependencias mockeadas
    private lateinit var repository: AlbumRepository
    
    // Datos de prueba
    private val testAlbums = listOf(
        Album(
            id = 1,
            name = "Buscando América",
            cover = "https://example.com/cover1.jpg",
            releaseDate = "1984-08-01",
            description = "Descripción",
            genre = "Salsa",
            recordLabel = "Elektra",
            performers = listOf(
                Performer(1, "Rubén Blades", "img.jpg", "Desc")
            )
        ),
        Album(
            id = 2,
            name = "A Night at the Opera",
            cover = "https://example.com/cover2.jpg",
            releaseDate = "1975-11-21",
            description = "Descripción",
            genre = "Rock",
            recordLabel = "EMI",
            performers = null
        )
    )
    
    @Before
    fun setup() {
        // Configurar el dispatcher de prueba
        Dispatchers.setMain(testDispatcher)
        
        // Crear mock del repository
        repository = mockk()
    }
    
    @After
    fun tearDown() {
        // Restaurar el dispatcher principal
        Dispatchers.resetMain()
    }
    
    /**
     * Test: Verificar que el ViewModel carga los álbumes al inicializarse
     */
    @Test
    fun `init triggers loadAlbums`() = runTest {
        // Given: Repository retorna un flow con Success
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Success(testAlbums)
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe haber llamado al repository
        verify(exactly = 1) { repository.getAlbums() }
    }
    
    /**
     * Test: Verificar que el estado inicial es Loading
     */
    @Test
    fun `uiState is Loading initially when loading albums`() = runTest {
        // Given: Repository emite Loading
        every { repository.getAlbums() } returns flowOf(Result.Loading)
        
        // When: Creamos el ViewModel
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe ser Loading
        val state = viewModel.uiState.value
        assertTrue("Should be loading", state.isLoading)
        assertNull("Error should be null", state.error)
    }
    
    /**
     * Test: Verificar que el estado se actualiza a Success con los datos correctos
     */
    @Test
    fun `uiState updates to Success with albums when repository returns data`() = runTest {
        // Given: Repository retorna Success con datos
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Success(testAlbums)
        )
        
        // When: Creamos el ViewModel y esperamos que procese
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe contener los álbumes
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Error should be null", state.error)
        assertFalse("Should not be empty", state.isEmpty)
        assertEquals("Should have 2 albums", 2, state.albums.size)
        assertEquals("First album name should match", "Buscando América", state.albums[0].name)
    }
    
    /**
     * Test: Verificar que el estado se actualiza a Error cuando hay un fallo
     */
    @Test
    fun `uiState updates to Error when repository returns error`() = runTest {
        // Given: Repository retorna Error
        val errorMessage = "Error de conexión: Network unavailable"
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Error(
                exception = Exception("Network unavailable"),
                message = errorMessage
            )
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe mostrar el error
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Error should not be null", state.error)
        assertEquals("Error message should match", errorMessage, state.error)
    }
    
    /**
     * Test: Verificar que isEmpty es true cuando la lista está vacía
     */
    @Test
    fun `uiState isEmpty is true when repository returns empty list`() = runTest {
        // Given: Repository retorna lista vacía
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Success(emptyList())
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: isEmpty debe ser true
        val state = viewModel.uiState.value
        assertTrue("isEmpty should be true", state.isEmpty)
        assertTrue("Albums list should be empty", state.albums.isEmpty())
    }
    
    /**
     * Test: Verificar que loadAlbums() puede ser llamado manualmente para reintentar
     */
    @Test
    fun `loadAlbums can be called manually to retry`() = runTest {
        // Given: Repository inicialmente retorna Error, luego Success
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Error(Exception("First error"), "Error inicial")
        ) andThen flowOf(
            Result.Loading,
            Result.Success(testAlbums)
        )
        
        // When: Creamos el ViewModel (primera carga con error)
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que hay error
        assertNotNull("Should have error", viewModel.uiState.value.error)
        
        // Llamar a loadAlbums manualmente para reintentar
        viewModel.loadAlbums()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe haber llamado al repository dos veces
        verify(exactly = 2) { repository.getAlbums() }
        
        // El estado debe tener los datos ahora
        val state = viewModel.uiState.value
        assertNull("Error should be null after retry", state.error)
        assertEquals("Should have albums after retry", 2, state.albums.size)
    }
    
    /**
     * Test: Verificar que clearError() limpia el mensaje de error
     */
    @Test
    fun `clearError removes error message from state`() = runTest {
        // Given: ViewModel con un error
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Error(Exception("Error"), "Error de prueba")
        )
        
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que hay error
        assertNotNull("Should have error", viewModel.uiState.value.error)
        
        // When: Llamamos a clearError()
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El error debe ser null
        assertNull("Error should be null after clearError", viewModel.uiState.value.error)
    }
    
    /**
     * Test: Verificar que el estado mantiene los álbumes cuando hay un error después de una carga exitosa
     */
    @Test
    fun `state keeps albums when error occurs after successful load`() = runTest {
        // Given: Primera carga exitosa
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Success(testAlbums)
        )
        
        viewModel = AlbumViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("Should have 2 albums", 2, viewModel.uiState.value.albums.size)
        
        // When: Segunda carga con error
        every { repository.getAlbums() } returns flowOf(
            Result.Loading,
            Result.Error(Exception("Error"), "Error en recarga")
        )
        
        viewModel.loadAlbums()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe mantener los álbumes y mostrar el error
        val state = viewModel.uiState.value
        assertNotNull("Should have error", state.error)
        assertEquals("Should still have 2 albums", 2, state.albums.size)
        assertFalse("isEmpty should be false", state.isEmpty)
    }
}
