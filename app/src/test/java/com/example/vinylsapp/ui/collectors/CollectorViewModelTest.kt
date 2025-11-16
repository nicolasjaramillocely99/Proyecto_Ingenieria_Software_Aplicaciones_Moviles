package com.example.vinylsapp.ui.collectors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinylsapp.data.model.Collector
import com.example.vinylsapp.data.repository.CollectorRepository
import com.example.vinylsapp.data.repository.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: CollectorRepository
    private lateinit var viewModel: CollectorViewModel

    private val sampleCollectors = listOf(
        Collector(
            id = 1,
            name = "Test Collector 1",
            avatarUrl = "https://example.com/avatar1.jpg",
            country = "Colombia",
            city = "Bogotá",
            shortBio = "Bio 1",
            totalAlbums = 10
        ),
        Collector(
            id = 2,
            name = "Test Collector 2",
            avatarUrl = "https://example.com/avatar2.jpg",
            country = "Chile",
            city = "Santiago",
            shortBio = "Bio 2",
            totalAlbums = 5
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers loadCollectors`() = runTest {
        every { repository.getCollectors() } returns flowOf(
            Result.Loading,
            Result.Success(sampleCollectors)
        )

        viewModel = CollectorViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 1) { repository.getCollectors() }
    }

    @Test
    fun `uiState is loading while repository emits loading`() = runTest {
        every { repository.getCollectors() } returns flowOf(Result.Loading)

        viewModel = CollectorViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `uiState updates with collectors when success`() = runTest {
        every { repository.getCollectors() } returns flowOf(
            Result.Loading,
            Result.Success(sampleCollectors)
        )

        viewModel = CollectorViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.collectors.size)
        assertFalse(state.isEmpty)
        assertEquals("Test Collector 1", state.collectors.first().name)
    }

    @Test
    fun `uiState updates with error when repository fails`() = runTest {
        val errorMessage = "Error de red"
        every { repository.getCollectors() } returns flowOf(
            Result.Loading,
            Result.Error(Exception("Network"), errorMessage)
        )

        viewModel = CollectorViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `uiState marks empty when repository returns empty list`() = runTest {
        every { repository.getCollectors() } returns flowOf(
            Result.Loading,
            Result.Success(emptyList())
        )

        viewModel = CollectorViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
        assertTrue(state.collectors.isEmpty())
    }
}
