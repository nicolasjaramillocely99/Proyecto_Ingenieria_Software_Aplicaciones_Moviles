package com.example.vinylsapp.ui.collectors

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.vinylsapp.MainDispatcherRule
import com.example.vinylsapp.data.model.Collector
import com.example.vinylsapp.data.model.FeaturedAlbum
import com.example.vinylsapp.data.repository.CollectorRepository
import com.example.vinylsapp.data.repository.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CollectorRepository = mockk()

    private fun buildViewModel(collectorId: Int = 1): CollectorDetailViewModel {
        return CollectorDetailViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(mapOf("collectorId" to collectorId))
        )
    }

    @Test
    fun `loadCollector updates state on success`() = runTest {
        val collector = Collector(
            id = 1,
            name = "Tester",
            avatarUrl = "https://example.com/avatar.jpg",
            country = "Colombia",
            city = "Bogotá",
            shortBio = "Bio test",
            totalAlbums = 5,
            favoriteGenres = listOf("Rock"),
            favoriteArtists = listOf("Artist"),
            featuredAlbums = listOf(
                FeaturedAlbum(
                    id = 10,
                    title = "Album",
                    artist = "Artist",
                    coverUrl = "url"
                )
            )
        )

        every { repository.getCollectorDetail(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(collector))
        }

        val viewModel = buildViewModel()

        viewModel.uiState.test {
            // Primer estado: loading
            val initial = awaitItem()
            assertTrue(initial.isLoading)
            assertNull(initial.error)
            assertNull(initial.collector)

            // Segundo estado: success
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertEquals(collector, success.collector)
            assertNull(success.error)

            // Cerramos el flujo de prueba
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadCollector exposes error when repository fails`() = runTest {
        every { repository.getCollectorDetail(2) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(Exception("network"), message = "Error de conexión"))
        }

        val viewModel = buildViewModel(collectorId = 2)

        viewModel.uiState.test {
            // Primer estado: loading
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            assertNull(loading.error)
            assertNull(loading.collector)

            // Segundo estado: error
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Error de conexión", errorState.error)
            assertNull(errorState.collector)

            cancelAndConsumeRemainingEvents()
        }
    }
}
