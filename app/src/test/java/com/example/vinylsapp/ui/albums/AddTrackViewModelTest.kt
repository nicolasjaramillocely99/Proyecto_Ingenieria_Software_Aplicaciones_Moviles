package com.example.vinylsapp.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.vinylsapp.data.model.CreateTrackRequest
import com.example.vinylsapp.data.model.Track
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
 * Pruebas unitarias para AddTrackViewModel
 * 
 * Objetivo: Verificar que el ViewModel:
 * - Valida correctamente los campos del formulario (solo nombre es obligatorio)
 * - Actualiza el estado correctamente al modificar campos
 * - Maneja estados de Loading, Success y Error al crear un track
 * - Convierte correctamente la duración a segundos
 * - Maneja campos opcionales correctamente
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddTrackViewModelTest {
    
    // Regla para ejecutar tareas de forma síncrona en tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Dispatcher de prueba para controlar las coroutines
    private val testDispatcher = StandardTestDispatcher()
    
    // System Under Test
    private lateinit var viewModel: AddTrackViewModel
    
    // Dependencias mockeadas
    private lateinit var repository: AlbumRepository
    private lateinit var savedStateHandle: SavedStateHandle
    
    // Datos de prueba
    private val albumId = 1
    private val validTrackRequest = CreateTrackRequest(
        name = "Test Track",
        duration = "03:45",
        albumId = albumId,
        seconds = 225, // 3 minutos y 45 segundos
        number = 1,
        composer = "Test Composer"
    )
    
    private val createdTrack = Track(
        id = 1,
        name = "Test Track",
        duration = "03:45",
        albumId = albumId,
        seconds = 225,
        number = 1,
        composer = "Test Composer"
    )
    
    @Before
    fun setup() {
        // Configurar el dispatcher de prueba
        Dispatchers.setMain(testDispatcher)
        
        // Crear mock del repository
        repository = mockk()
        
        // Crear SavedStateHandle con albumId
        savedStateHandle = SavedStateHandle(mapOf("albumId" to albumId))
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
    fun `initial state is empty`() = runTest(testDispatcher.scheduler) {
        // When: Creamos el ViewModel
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Todos los campos deben estar vacíos
        val state = viewModel.uiState.value
        assertTrue("Name should be empty", state.name.isEmpty())
        assertTrue("Duration should be empty", state.duration.isEmpty())
        assertTrue("Number should be empty", state.number.isEmpty())
        assertTrue("Composer should be empty", state.composer.isEmpty())
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Error should be null", state.error)
        assertFalse("Should not be success", state.isSuccess)
        assertFalse("Form should not be valid", state.isValid())
    }
    
    /**
     * Test: Verificar que updateName actualiza el nombre correctamente
     */
    @Test
    fun `updateName updates name field`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel inicializado
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        
        // When: Actualizamos el nombre
        viewModel.updateName("New Track Name")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El nombre debe actualizarse
        assertEquals("Name should be updated", "New Track Name", viewModel.uiState.value.name)
        assertNull("Error should be cleared", viewModel.uiState.value.error)
    }
    
    /**
     * Test: Verificar que updateDuration actualiza la duración
     */
    @Test
    fun `updateDuration updates duration field`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel inicializado
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        
        // When: Actualizamos la duración
        viewModel.updateDuration("03:45")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: La duración debe actualizarse
        assertEquals("Duration should be updated", "03:45", viewModel.uiState.value.duration)
    }
    
    /**
     * Test: Verificar que updateNumber actualiza el número de pista
     */
    @Test
    fun `updateNumber updates number field`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel inicializado
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        
        // When: Actualizamos el número
        viewModel.updateNumber("5")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El número debe actualizarse
        assertEquals("Number should be updated", "5", viewModel.uiState.value.number)
    }
    
    /**
     * Test: Verificar que updateComposer actualiza el compositor
     */
    @Test
    fun `updateComposer updates composer field`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel inicializado
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        
        // When: Actualizamos el compositor
        viewModel.updateComposer("John Doe")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El compositor debe actualizarse
        assertEquals("Composer should be updated", "John Doe", viewModel.uiState.value.composer)
    }
    
    /**
     * Test: Verificar que isValid retorna false cuando el nombre está vacío
     */
    @Test
    fun `isValid returns false when name is empty`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con nombre vacío pero otros campos llenos
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateDuration("03:45")
        viewModel.updateNumber("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El formulario no debe ser válido
        assertFalse("Form should not be valid", viewModel.uiState.value.isValid())
    }
    
    /**
     * Test: Verificar que isValid retorna true cuando solo el nombre está lleno
     */
    @Test
    fun `isValid returns true when only name is filled`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con solo el nombre lleno
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateName("Test Track")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El formulario debe ser válido
        assertTrue("Form should be valid", viewModel.uiState.value.isValid())
    }
    
    /**
     * Test: Verificar que getDurationInSeconds convierte MM:SS correctamente
     */
    @Test
    fun `getDurationInSeconds converts MM SS format correctly`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con duración en formato MM:SS
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateDuration("03:45")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe convertir a 225 segundos (3*60 + 45)
        val seconds = viewModel.uiState.value.getDurationInSeconds()
        assertEquals("Should convert to 225 seconds", 225, seconds)
    }
    
    /**
     * Test: Verificar que getDurationInSeconds convierte HH:MM:SS correctamente
     */
    @Test
    fun `getDurationInSeconds converts HH MM SS format correctly`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con duración en formato HH:MM:SS
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateDuration("01:03:45")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe convertir a 3825 segundos (1*3600 + 3*60 + 45)
        val seconds = viewModel.uiState.value.getDurationInSeconds()
        assertEquals("Should convert to 3825 seconds", 3825, seconds)
    }
    
    /**
     * Test: Verificar que getDurationInSeconds retorna null para formato inválido
     */
    @Test
    fun `getDurationInSeconds returns null for invalid format`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con duración en formato inválido
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateDuration("invalid")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe retornar null
        val seconds = viewModel.uiState.value.getDurationInSeconds()
        assertNull("Should return null for invalid format", seconds)
    }
    
    /**
     * Test: Verificar que getTrackNumber retorna el número como Int
     */
    @Test
    fun `getTrackNumber returns number as Int`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con número de pista
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateNumber("5")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe retornar 5
        val number = viewModel.uiState.value.getTrackNumber()
        assertEquals("Should return 5", 5, number)
    }
    
    /**
     * Test: Verificar que getTrackNumber retorna null cuando está vacío
     */
    @Test
    fun `getTrackNumber returns null when empty`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel sin número de pista
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe retornar null
        val number = viewModel.uiState.value.getTrackNumber()
        assertNull("Should return null when empty", number)
    }
    
    /**
     * Test: Verificar que createTrack muestra error cuando el formulario no es válido
     */
    @Test
    fun `createTrack shows error when form is invalid`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con formulario incompleto (sin nombre)
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateDuration("03:45")
        testDispatcher.scheduler.advanceUntilIdle()
        
        var successCallbackCalled = false
        
        // When: Intentamos crear el track
        viewModel.createTrack(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe mostrar error de validación
        assertNotNull("Should have validation error", viewModel.uiState.value.error)
        assertTrue("Error should mention name is required", 
            viewModel.uiState.value.error?.contains("obligatorio") == true)
        assertFalse("Success callback should not be called", successCallbackCalled)
        
        // Repository no debe ser llamado
        verify(exactly = 0) { repository.createTrack(any(), any()) }
    }
    
    /**
     * Test: Verificar que createTrack muestra error cuando albumId es inválido
     */
    @Test
    fun `createTrack shows error when albumId is invalid`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con albumId inválido
        val invalidSavedStateHandle = SavedStateHandle(mapOf("albumId" to 0))
        viewModel = AddTrackViewModel(repository, invalidSavedStateHandle)
        viewModel.updateName("Test Track")
        testDispatcher.scheduler.advanceUntilIdle()
        
        var successCallbackCalled = false
        
        // When: Intentamos crear el track
        viewModel.createTrack(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Debe mostrar error
        assertNotNull("Should have error", viewModel.uiState.value.error)
        assertTrue("Error should mention invalid album ID", 
            viewModel.uiState.value.error?.contains("álbum") == true)
        assertFalse("Success callback should not be called", successCallbackCalled)
    }
    
    /**
     * Test: Verificar que createTrack emite Loading y luego Success cuando es exitoso
     */
    @Test
    fun `createTrack emits Loading then Success when creation is successful`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con formulario válido y repository que retorna Success
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateName(validTrackRequest.name)
        viewModel.updateDuration(validTrackRequest.duration ?: "")
        viewModel.updateNumber(validTrackRequest.number?.toString() ?: "")
        viewModel.updateComposer(validTrackRequest.composer ?: "")
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createTrack(albumId, any()) } returns flowOf(
            Result.Loading,
            Result.Success(createdTrack)
        )
        
        var successCallbackCalled = false
        
        // When: Creamos el track
        viewModel.createTrack(onSuccess = { successCallbackCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Finalmente debe ser Success
        val finalState = viewModel.uiState.value
        assertFalse("Should not be loading", finalState.isLoading)
        assertNull("Error should be null", finalState.error)
        assertTrue("Should be success", finalState.isSuccess)
        assertTrue("Success callback should be called", successCallbackCalled)
        
        // Verificar que se llamó al repository con los datos correctos
        verify(exactly = 1) { 
            repository.createTrack(albumId, match { request ->
                request.name == validTrackRequest.name.trim() &&
                request.albumId == albumId
            })
        }
    }
    
    /**
     * Test: Verificar que createTrack maneja errores correctamente
     */
    @Test
    fun `createTrack handles errors correctly`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con formulario válido y repository que retorna Error
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateName("Test Track")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val errorMessage = "Error al crear el track"
        every { repository.createTrack(albumId, any()) } returns flowOf(
            Result.Loading,
            Result.Error(
                exception = Exception("Network error"),
                message = errorMessage
            )
        )
        
        var successCallbackCalled = false
        
        // When: Creamos el track
        viewModel.createTrack(onSuccess = { successCallbackCalled = true })
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
     * Test: Verificar que createTrack maneja campos opcionales correctamente
     */
    @Test
    fun `createTrack handles optional fields correctly`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con solo el nombre (campos opcionales vacíos)
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateName("Test Track")
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createTrack(albumId, any()) } returns flowOf(
            Result.Loading,
            Result.Success(createdTrack)
        )
        
        // When: Creamos el track
        viewModel.createTrack(onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El repository debe recibir null para campos opcionales
        verify(exactly = 1) { 
            repository.createTrack(albumId, match { request ->
                request.name == "Test Track" &&
                request.duration == null &&
                request.seconds == null &&
                request.number == null &&
                request.composer == null
            })
        }
    }
    
    /**
     * Test: Verificar que clearSuccess limpia el estado de éxito
     */
    @Test
    fun `clearSuccess removes success state`() = runTest(testDispatcher.scheduler) {
        // Given: ViewModel con estado de éxito
        viewModel = AddTrackViewModel(repository, savedStateHandle)
        viewModel.updateName("Test Track")
        testDispatcher.scheduler.advanceUntilIdle()
        
        every { repository.createTrack(albumId, any()) } returns flowOf(
            Result.Loading,
            Result.Success(createdTrack)
        )
        
        viewModel.createTrack(onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue("Should be success", viewModel.uiState.value.isSuccess)
        
        // When: Llamamos a clearSuccess
        viewModel.clearSuccess()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: El estado de éxito debe ser false
        assertFalse("Success should be false after clearSuccess", viewModel.uiState.value.isSuccess)
    }
}

