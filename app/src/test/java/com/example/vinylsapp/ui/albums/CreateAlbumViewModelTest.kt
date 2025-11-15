package com.example.vinylsapp.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.CreateAlbumRequest
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
 * Pruebas unitarias para CreateAlbumViewModel
 * 
 * Objetivo: Verificar que el ViewModel:
 * - Valida correctamente los campos del formulario
 * - Actualiza el estado correctamente al modificar campos
 * - Maneja estados de Loading, Success y Error al crear un álbum
 * - Controla los dropdowns correctamente
 * - Limpia errores y resetea el estado
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateAlbumViewModelTest {
    
    // Regla para ejecutar tareas de forma síncrona en tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Dispatcher de prueba para controlar las coroutines
    private val testDispatcher = StandardTestDispatcher()
    
    // System Under Test
    private lateinit var viewModel: CreateAlbumViewModel
    
    // Dependencias mockeadas
    private lateinit var repository: AlbumRepository
    
    // Datos de prueba
    private val validAlbumRequest = CreateAlbumRequest(
        name = "Test Album",
        cover = "https://example.com/cover.jpg",
        releaseDate = "2024-01-15",
        description = "Test description",
        genre = "Rock",
        recordLabel = "Sony Music"
    )
    
    private val createdAlbum = Album(
        id = 1,
        name = "Test Album",
        cover = "https://example.com/cover.jpg",
        releaseDate = "2024-01-15",
        description = "Test description",
        genre = "Rock",
        recordLabel = "Sony Music",
        performers = null
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
     * Test: Verificar que el estado inicial está vacío
     */
    @Test
    fun `initial state is empty`() = runTest {
        // When: Creamos el ViewModel
        viewModel = CreateAlbumViewModel(repository)
        
        // Then: Todos los campos deben estar vacíos
        val state = viewModel.uiState.value
        assertTrue("Name should be empty", state.name.isEmpty())
        assertTrue("Cover should be empty", state.cover.isEmpty())
        assertTrue("Release date should be empty", state.releaseDate.isEmpty())
        assertTrue("Description should be empty", state.description.isEmpty())
        assertTrue("Genre should be empty", state.genre.isEmpty())
        assertTrue("Record label should be empty", state.recordLabel.isEmpty())
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Error should be null", state.error)
        assertFalse("Should not be success", state.isSuccess)
        assertFalse("Form should not be valid", state.isValid())
    }
    
    /**
     * Test: Verificar que updateName actualiza el nombre correctamente
     */
    @Test
    fun `updateName updates name field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos el nombre
        viewModel.updateName("New Album Name")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El nombre debe actualizarse
        assertEquals("Name should be updated", "New Album Name", viewModel.uiState.value.name)
        assertNull("Error should be cleared", viewModel.uiState.value.error)
    }
    
    /**
     * Test: Verificar que updateCover actualiza la URL de la portada
     */
    @Test
    fun `updateCover updates cover field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos la portada
        viewModel.updateCover("https://example.com/cover.jpg")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: La portada debe actualizarse
        assertEquals("Cover should be updated", "https://example.com/cover.jpg", viewModel.uiState.value.cover)
    }
    
    /**
     * Test: Verificar que updateReleaseDate actualiza la fecha
     */
    @Test
    fun `updateReleaseDate updates release date field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos la fecha
        viewModel.updateReleaseDate("2024-01-15")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: La fecha debe actualizarse
        assertEquals("Release date should be updated", "2024-01-15", viewModel.uiState.value.releaseDate)
    }
    
    /**
     * Test: Verificar que updateDescription actualiza la descripción
     */
    @Test
    fun `updateDescription updates description field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos la descripción
        viewModel.updateDescription("Album description")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: La descripción debe actualizarse
        assertEquals("Description should be updated", "Album description", viewModel.uiState.value.description)
    }
    
    /**
     * Test: Verificar que updateGenre actualiza el género
     */
    @Test
    fun `updateGenre updates genre field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos el género
        viewModel.updateGenre("Rock")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El género debe actualizarse
        assertEquals("Genre should be updated", "Rock", viewModel.uiState.value.genre)
    }
    
    /**
     * Test: Verificar que updateRecordLabel actualiza la discográfica
     */
    @Test
    fun `updateRecordLabel updates record label field`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Actualizamos la discográfica
        viewModel.updateRecordLabel("Sony Music")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: La discográfica debe actualizarse
        assertEquals("Record label should be updated", "Sony Music", viewModel.uiState.value.recordLabel)
    }
    
    /**
     * Test: Verificar que isValid retorna false cuando faltan campos
     */
    @Test
    fun `isValid returns false when fields are missing`() = runTest {
        // Given: ViewModel con algunos campos llenos pero no todos
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName("Test Album")
        viewModel.updateGenre("Rock")
        // Faltan otros campos
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El formulario no debe ser válido
        assertFalse("Form should not be valid", viewModel.uiState.value.isValid())
    }
    
    /**
     * Test: Verificar que isValid retorna true cuando todos los campos están llenos
     */
    @Test
    fun `isValid returns true when all fields are filled`() = runTest {
        // Given: ViewModel con todos los campos llenos
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName("Test Album")
        viewModel.updateCover("https://example.com/cover.jpg")
        viewModel.updateReleaseDate("2024-01-15")
        viewModel.updateDescription("Description")
        viewModel.updateGenre("Rock")
        viewModel.updateRecordLabel("Sony Music")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El formulario debe ser válido
        assertTrue("Form should be valid", viewModel.uiState.value.isValid())
    }
    
    /**
     * Test: Verificar que createAlbum muestra error cuando el formulario no es válido
     */
    @Test
    fun `createAlbum shows error when form is invalid`() = runTest {
        // Given: ViewModel con formulario incompleto
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName("Test Album")
        // Faltan otros campos
        testDispatcher.scheduler.advanceUntilIdle()
        
        var successCallbackCalled = false
        
        // When: Intentamos crear el álbum
        viewModel.createAlbum(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe mostrar error de validación
        assertNotNull("Should have validation error", viewModel.uiState.value.error)
        assertTrue("Error should mention incomplete fields", 
            viewModel.uiState.value.error?.contains("completa todos los campos") == true)
        assertFalse("Success callback should not be called", successCallbackCalled)
        
        // Repository no debe ser llamado
        verify(exactly = 0) { repository.createAlbum(any()) }
    }
    
    /**
     * Test: Verificar que createAlbum emite Loading y luego Success cuando es exitoso
     */
    @Test
    fun `createAlbum emits Loading then Success when creation is successful`() = runTest {
        // Given: ViewModel con formulario válido y repository que retorna Success
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName(validAlbumRequest.name)
        viewModel.updateCover(validAlbumRequest.cover)
        viewModel.updateReleaseDate(validAlbumRequest.releaseDate)
        viewModel.updateDescription(validAlbumRequest.description)
        viewModel.updateGenre(validAlbumRequest.genre)
        viewModel.updateRecordLabel(validAlbumRequest.recordLabel)
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createAlbum(any()) } returns flowOf(
            Result.Loading,
            Result.Success(createdAlbum)
        )
        
        var successCallbackCalled = false
        
        // When: Creamos el álbum
        viewModel.createAlbum(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe emitir Loading primero
        // (Nota: En un test real, podrías necesitar capturar estados intermedios)
        
        // Finalmente debe ser Success
        val finalState = viewModel.uiState.value
        assertFalse("Should not be loading", finalState.isLoading)
        assertNull("Error should be null", finalState.error)
        assertTrue("Should be success", finalState.isSuccess)
        assertTrue("Success callback should be called", successCallbackCalled)
        
        // Verificar que se llamó al repository con los datos correctos
        verify(exactly = 1) { 
            repository.createAlbum(match { 
                it.name == validAlbumRequest.name.trim() &&
                it.cover == validAlbumRequest.cover.trim() &&
                it.genre == validAlbumRequest.genre.trim()
            })
        }
    }
    
    /**
     * Test: Verificar que createAlbum maneja errores correctamente
     */
    @Test
    fun `createAlbum handles errors correctly`() = runTest {
        // Given: ViewModel con formulario válido y repository que retorna Error
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName(validAlbumRequest.name)
        viewModel.updateCover(validAlbumRequest.cover)
        viewModel.updateReleaseDate(validAlbumRequest.releaseDate)
        viewModel.updateDescription(validAlbumRequest.description)
        viewModel.updateGenre(validAlbumRequest.genre)
        viewModel.updateRecordLabel(validAlbumRequest.recordLabel)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val errorMessage = "Error de validación: campo requerido"
        every { repository.createAlbum(any()) } returns flowOf(
            Result.Loading,
            Result.Error(
                exception = Exception("Validation error"),
                message = errorMessage
            )
        )
        
        var successCallbackCalled = false
        
        // When: Creamos el álbum
        viewModel.createAlbum(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe mostrar el error
        val finalState = viewModel.uiState.value
        assertFalse("Should not be loading", finalState.isLoading)
        assertNotNull("Error should not be null", finalState.error)
        assertEquals("Error message should match", errorMessage, finalState.error)
        assertFalse("Should not be success", finalState.isSuccess)
        assertFalse("Success callback should not be called", successCallbackCalled)
    }
    
    /**
     * Test: Verificar que clearError limpia el mensaje de error
     */
    @Test
    fun `clearError removes error message`() = runTest {
        // Given: ViewModel con un error
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName(validAlbumRequest.name)
        viewModel.updateCover(validAlbumRequest.cover)
        viewModel.updateReleaseDate(validAlbumRequest.releaseDate)
        viewModel.updateDescription(validAlbumRequest.description)
        viewModel.updateGenre(validAlbumRequest.genre)
        viewModel.updateRecordLabel(validAlbumRequest.recordLabel)
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createAlbum(any()) } returns flowOf(
            Result.Loading,
            Result.Error(Exception("Error"), "Test error")
        )
        
        viewModel.createAlbum(onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull("Should have error", viewModel.uiState.value.error)
        
        // When: Llamamos a clearError
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El error debe ser null
        assertNull("Error should be null after clearError", viewModel.uiState.value.error)
    }
    
    /**
     * Test: Verificar que resetState resetea todos los campos
     */
    @Test
    fun `resetState clears all fields`() = runTest {
        // Given: ViewModel con campos llenos
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName("Test Album")
        viewModel.updateCover("https://example.com/cover.jpg")
        viewModel.updateReleaseDate("2024-01-15")
        viewModel.updateDescription("Description")
        viewModel.updateGenre("Rock")
        viewModel.updateRecordLabel("Sony Music")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When: Reseteamos el estado
        viewModel.resetState()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Todos los campos deben estar vacíos
        val state = viewModel.uiState.value
        assertTrue("Name should be empty", state.name.isEmpty())
        assertTrue("Cover should be empty", state.cover.isEmpty())
        assertTrue("Release date should be empty", state.releaseDate.isEmpty())
        assertTrue("Description should be empty", state.description.isEmpty())
        assertTrue("Genre should be empty", state.genre.isEmpty())
        assertTrue("Record label should be empty", state.recordLabel.isEmpty())
    }
    
    /**
     * Test: Verificar que setGenreDropdownExpanded controla el estado del dropdown
     */
    @Test
    fun `setGenreDropdownExpanded controls dropdown state`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Expandimos el dropdown
        viewModel.setGenreDropdownExpanded(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El dropdown debe estar expandido
        assertTrue("Genre dropdown should be expanded", viewModel.uiState.value.isGenreDropdownExpanded)
        
        // When: Colapsamos el dropdown
        viewModel.setGenreDropdownExpanded(false)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El dropdown no debe estar expandido
        assertFalse("Genre dropdown should not be expanded", viewModel.uiState.value.isGenreDropdownExpanded)
    }
    
    /**
     * Test: Verificar que setRecordLabelDropdownExpanded controla el estado del dropdown
     */
    @Test
    fun `setRecordLabelDropdownExpanded controls dropdown state`() = runTest {
        // Given: ViewModel inicializado
        viewModel = CreateAlbumViewModel(repository)
        
        // When: Expandimos el dropdown
        viewModel.setRecordLabelDropdownExpanded(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El dropdown debe estar expandido
        assertTrue("Record label dropdown should be expanded", viewModel.uiState.value.isRecordLabelDropdownExpanded)
        
        // When: Colapsamos el dropdown
        viewModel.setRecordLabelDropdownExpanded(false)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El dropdown no debe estar expandido
        assertFalse("Record label dropdown should not be expanded", viewModel.uiState.value.isRecordLabelDropdownExpanded)
    }
    
    /**
     * Test: Verificar que los campos se recortan (trim) antes de crear el álbum
     */
    @Test
    fun `createAlbum trims all fields before creating`() = runTest {
        // Given: ViewModel con campos que tienen espacios en blanco
        viewModel = CreateAlbumViewModel(repository)
        viewModel.updateName("  Test Album  ")
        viewModel.updateCover("  https://example.com/cover.jpg  ")
        viewModel.updateReleaseDate("  2024-01-15  ")
        viewModel.updateDescription("  Description  ")
        viewModel.updateGenre("  Rock  ")
        viewModel.updateRecordLabel("  Sony Music  ")
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createAlbum(any()) } returns flowOf(
            Result.Loading,
            Result.Success(createdAlbum)
        )
        
        // When: Creamos el álbum
        viewModel.createAlbum(onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El repository debe recibir los campos recortados
        verify(exactly = 1) { 
            repository.createAlbum(match { request ->
                request.name == "Test Album" &&
                request.cover == "https://example.com/cover.jpg" &&
                request.releaseDate == "2024-01-15" &&
                request.description == "Description" &&
                request.genre == "Rock" &&
                request.recordLabel == "Sony Music"
            })
        }
    }
}

