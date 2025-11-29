package com.example.vinylsapp.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.Performer
import com.example.vinylsapp.data.model.Track
import com.example.vinylsapp.data.repository.AlbumRepository
import com.example.vinylsapp.data.repository.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias para AlbumDetailViewModel
 * 
 * Objetivo: Verificar que el ViewModel:
 * - Carga los detalles del álbum al iniciar con un albumId válido
 * - No carga si el albumId es inválido (0 o negativo)
 * - Actualiza el estado de la UI correctamente según las respuestas
 * - Maneja estados de Loading, Success y Error
 * - Permite seleccionar/deseleccionar tracks
 * - Permite reintentar la carga
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {
    
    // Regla para ejecutar tareas de forma síncrona en tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Dispatcher de prueba para controlar las coroutines
    private val testDispatcher = StandardTestDispatcher()
    
    // System Under Test
    private lateinit var viewModel: AlbumDetailViewModel
    
    // Dependencias mockeadas
    private lateinit var repository: AlbumRepository
    private lateinit var savedStateHandle: SavedStateHandle
    
    // Datos de prueba
    private val testAlbum = Album(
        id = 1,
        name = "Melodías del Alma",
        cover = "https://example.com/cover.jpg",
        releaseDate = "2023-01-15",
        description = "El album Melodias del alma es el primer álbum en la historia de la artista con más de 15 millones de reproducciones",
        genre = "Pop",
        recordLabel = "Sony Music",
        performers = listOf(
            Performer(1, "Aurora", "https://example.com/aurora.jpg", "Artista pop")
        ),
        tracks = listOf(
            Track(1, "Amanecer", "3:45", 1),
            Track(2, "Sueños de Verano", "4:12", 1),
            Track(3, "Noche Estrellada", "5:01", 1)
        )
    )
    
    @Before
    fun setup() {
        // Configurar el dispatcher de prueba
        Dispatchers.setMain(testDispatcher)
        
        // Crear mocks
        repository = mockk()
        savedStateHandle = mockk()
        
        // Mock getStateFlow para track_created (usado en init del ViewModel)
        every { savedStateHandle.getStateFlow<Boolean?>("track_created", null) } returns MutableStateFlow<Boolean?>(null)
    }
    
    @After
    fun tearDown() {
        // Restaurar el dispatcher principal
        Dispatchers.resetMain()
    }
    
    /**
     * Test: Verificar que el ViewModel carga los detalles al inicializarse con albumId válido
     */
    @Test
    fun `init triggers loadAlbumDetails when albumId is valid`() = runTest(testDispatcher.scheduler) {
        // Given: SavedStateHandle con albumId válido
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe haber llamado al repository
        verify(exactly = 1) { repository.getAlbumById(1) }
    }
    
    /**
     * Test: Verificar que el ViewModel NO carga si el albumId es 0
     */
    @Test
    fun `init does not trigger loadAlbumDetails when albumId is zero`() = runTest(testDispatcher.scheduler) {
        // Given: SavedStateHandle con albumId = 0
        every { savedStateHandle.get<Int>("albumId") } returns 0
        
        // When: Creamos el ViewModel
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: NO debe haber llamado al repository
        verify(exactly = 0) { repository.getAlbumById(any()) }
    }
    
    /**
     * Test: Verificar que el estado inicial es Loading cuando se carga el álbum
     */
    @Test
    fun `uiState is Loading initially when loading album details`() = runTest(testDispatcher.scheduler) {
        // Given: Repository emite Loading
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(Result.Loading)
        
        // When: Creamos el ViewModel
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe ser Loading
        val state = viewModel.uiState.value
        assertTrue("Should be loading", state.isLoading)
        assertNull("Error should be null", state.error)
        assertNull("Album should be null", state.album)
    }
    
    /**
     * Test: Verificar que el estado se actualiza a Success con los datos correctos
     */
    @Test
    fun `uiState updates to Success with album when repository returns data`() = runTest(testDispatcher.scheduler) {
        // Given: Repository retorna Success con datos
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        // When: Creamos el ViewModel y esperamos que procese
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe contener el álbum
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Error should be null", state.error)
        assertNotNull("Album should not be null", state.album)
        assertEquals("Album name should match", "Melodías del Alma", state.album?.name)
        assertEquals("Should have 3 tracks", 3, state.album?.tracks?.size)
    }
    
    /**
     * Test: Verificar que el estado se actualiza a Error cuando hay un fallo
     */
    @Test
    fun `uiState updates to Error when repository returns error`() = runTest(testDispatcher.scheduler) {
        // Given: Repository retorna Error
        val errorMessage = "Error de conexión: Network unavailable"
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Error(
                exception = Exception("Network unavailable"),
                message = errorMessage
            )
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado debe mostrar el error
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Error should not be null", state.error)
        assertEquals("Error message should match", errorMessage, state.error)
        assertNull("Album should be null", state.album)
    }
    
    /**
     * Test: Verificar que selectTrack() selecciona una canción
     */
    @Test
    fun `selectTrack sets selectedTrackId`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con álbum cargado
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When: Seleccionamos un track
        viewModel.selectTrack(2)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El track debe estar seleccionado
        val state = viewModel.uiState.value
        assertEquals("Selected track ID should be 2", 2, state.selectedTrackId)
    }
    
    /**
     * Test: Verificar que selectTrack() deselecciona si se hace click en el mismo track
     */
    @Test
    fun `selectTrack deselects when clicking same track`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con track seleccionado
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Seleccionar track 2
        viewModel.selectTrack(2)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Track 2 should be selected", 2, viewModel.uiState.value.selectedTrackId)
        
        // When: Hacemos click en el mismo track
        viewModel.selectTrack(2)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El track debe estar deseleccionado
        val state = viewModel.uiState.value
        assertNull("Selected track ID should be null", state.selectedTrackId)
    }
    
    /**
     * Test: Verificar que selectTrack() cambia la selección a otro track
     */
    @Test
    fun `selectTrack changes selection to different track`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con track seleccionado
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Seleccionar track 1
        viewModel.selectTrack(1)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Track 1 should be selected", 1, viewModel.uiState.value.selectedTrackId)
        
        // When: Seleccionamos otro track
        viewModel.selectTrack(3)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El nuevo track debe estar seleccionado
        val state = viewModel.uiState.value
        assertEquals("Track 3 should be selected", 3, state.selectedTrackId)
    }
    
    /**
     * Test: Verificar que loadAlbumDetails() puede ser llamado manualmente para reintentar
     */
    @Test
    fun `loadAlbumDetails can be called manually to retry`() = runTest(testDispatcher.scheduler) {
        // Given: Repository inicialmente retorna Error, luego Success
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Error(Exception("First error"), "Error inicial")
        ) andThen flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        // When: Creamos el ViewModel (primera carga con error)
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que hay error
        assertNotNull("Should have error", viewModel.uiState.value.error)
        
        // Llamar a loadAlbumDetails manualmente para reintentar
        viewModel.loadAlbumDetails()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe haber llamado al repository dos veces
        verify(exactly = 2) { repository.getAlbumById(1) }
        
        // El estado debe tener los datos ahora
        val state = viewModel.uiState.value
        assertNull("Error should be null after retry", state.error)
        assertNotNull("Album should not be null after retry", state.album)
        assertEquals("Album name should match", "Melodías del Alma", state.album?.name)
    }
    
    /**
     * Test: Verificar que loadAlbumDetails() no hace nada si albumId es inválido
     */
    @Test
    fun `loadAlbumDetails does nothing when albumId is invalid`() = runTest(testDispatcher.scheduler) {
        // Given: SavedStateHandle con albumId = 0
        every { savedStateHandle.get<Int>("albumId") } returns 0
        
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When: Intentamos cargar manualmente
        viewModel.loadAlbumDetails()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: NO debe haber llamado al repository
        verify(exactly = 0) { repository.getAlbumById(any()) }
    }
    
    /**
     * Test: Verificar que el álbum contiene todos los tracks correctamente
     */
    @Test
    fun `album contains all tracks correctly`() = runTest(testDispatcher.scheduler) {
        // Given: Repository retorna álbum con tracks
        every { savedStateHandle.get<Int>("albumId") } returns 1
        every { repository.getAlbumById(1) } returns flowOf(
            Result.Loading,
            Result.Success(testAlbum)
        )
        
        // When: Creamos el ViewModel
        viewModel = AlbumDetailViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El álbum debe tener todos los tracks
        val state = viewModel.uiState.value
        val tracks = state.album?.tracks
        
        assertNotNull("Tracks should not be null", tracks)
        assertEquals("Should have 3 tracks", 3, tracks?.size)
        assertEquals("First track name", "Amanecer", tracks?.get(0)?.name)
        assertEquals("First track duration", "3:45", tracks?.get(0)?.duration)
        assertEquals("Second track name", "Sueños de Verano", tracks?.get(1)?.name)
        assertEquals("Third track name", "Noche Estrellada", tracks?.get(2)?.name)
    }
}

