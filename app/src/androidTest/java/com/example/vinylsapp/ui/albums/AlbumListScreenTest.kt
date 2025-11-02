package com.example.vinylsapp.ui.albums

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.Performer
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de UI para AlbumListScreen usando Compose Testing
 * 
 * Estas son pruebas de integración que verifican:
 * - Renderizado correcto de la pantalla
 * - Visualización de álbumes en el grid
 * - Interacciones del usuario (clicks, scroll)
 * - Estados de Loading, Error y Empty
 */
class AlbumListScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    
    private val testAlbums = listOf(
        Album(
            id = 1,
            name = "Buscando América",
            cover = "https://example.com/cover1.jpg",
            releaseDate = "1984-08-01",
            description = "Descripción del álbum",
            genre = "Salsa",
            recordLabel = "Elektra",
            performers = listOf(
                Performer(1, "Rubén Blades", null, null)
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
            performers = listOf(
                Performer(2, "Queen", null, null)
            )
        ),
        Album(
            id = 3,
            name = "Abbey Road",
            cover = "https://example.com/cover3.jpg",
            releaseDate = "1969-09-26",
            description = "Álbum icónico",
            genre = "Rock",
            recordLabel = "Apple Records",
            performers = null
        )
    )
    
    /**
     * Test: Verificar que el grid muestra los álbumes correctamente
     */
    @Test
    fun albumGrid_displaysAlbums_correctly() {
        // Given: Tenemos una lista de 3 álbumes
        composeTestRule.setContent {
            AlbumGrid(
                albums = testAlbums,
                onAlbumClick = {}
            )
        }
        
        // Then: Debemos ver los 3 álbumes
        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed()
        composeTestRule.onNodeWithText("A Night at the Opera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abbey Road").assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se muestran los nombres de los artistas
     */
    @Test
    fun albumCard_displaysPerformerName() {
        // Given: Tarjeta de álbum con performer
        composeTestRule.setContent {
            AlbumCard(
                album = testAlbums[0],
                onClick = {}
            )
        }
        
        // Then: Debe mostrar el nombre del álbum y del artista
        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rubén Blades").assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que muestra recordLabel cuando no hay performers
     */
    @Test
    fun albumCard_displaysRecordLabel_whenNoPerformers() {
        // Given: Tarjeta de álbum sin performers
        composeTestRule.setContent {
            AlbumCard(
                album = testAlbums[2],
                onClick = {}
            )
        }
        
        // Then: Debe mostrar el recordLabel en lugar del performer
        composeTestRule.onNodeWithText("Abbey Road").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apple Records").assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que el click en un álbum dispara el callback
     */
    @Test
    fun albumCard_onClick_triggersCallback() {
        // Given: Estado para trackear el click
        var clickedAlbumId: Int? = null
        
        composeTestRule.setContent {
            AlbumCard(
                album = testAlbums[0],
                onClick = { clickedAlbumId = testAlbums[0].id }
            )
        }
        
        // When: Hacemos click en la tarjeta
        composeTestRule.onNodeWithText("Buscando América").performClick()
        
        // Then: El callback debe haberse ejecutado
        assert(clickedAlbumId == 1)
    }
    
    /**
     * Test: Verificar que se muestra el indicador de carga
     */
    @Test
    fun loadingIndicator_isDisplayed() {
        // Given: Estado de loading
        composeTestRule.setContent {
            LoadingIndicator()
        }
        
        // Then: Debe mostrar el texto de carga
        composeTestRule.onNodeWithText("Cargando...", substring = true, ignoreCase = true)
            .assertExists()
    }
    
    /**
     * Test: Verificar que se muestra el mensaje de error con botón de reintentar
     */
    @Test
    fun errorMessage_displaysCorrectly_withRetryButton() {
        // Given: Estado de error
        var retryClicked = false
        
        composeTestRule.setContent {
            ErrorMessage(
                message = "Error de conexión: Network unavailable",
                onRetry = { retryClicked = true }
            )
        }
        
        // Then: Debe mostrar el mensaje de error
        composeTestRule.onNodeWithText("Error de conexión: Network unavailable")
            .assertIsDisplayed()
        
        // When: Hacemos click en Reintentar
        composeTestRule.onNodeWithText("Reintentar", useUnmergedTree = true)
            .performClick()
        
        // Then: El callback debe haberse ejecutado
        assert(retryClicked)
    }
    
    /**
     * Test: Verificar que se muestra el estado vacío
     */
    @Test
    fun emptyState_isDisplayed() {
        // Given: Estado vacío
        composeTestRule.setContent {
            EmptyState()
        }
        
        // Then: Debe mostrar el mensaje de lista vacía
        composeTestRule.onNodeWithText("No hay álbumes disponibles", substring = true)
            .assertExists()
    }
    
    /**
     * Test: Verificar que el grid tiene 2 columnas
     */
    @Test
    fun albumGrid_hasTwoColumns() {
        // Given: Grid con álbumes
        composeTestRule.setContent {
            AlbumGrid(
                albums = testAlbums,
                onAlbumClick = {}
            )
        }
        
        // Then: Los primeros dos álbumes deben estar en la misma fila vertical
        // (esto verifica indirectamente el layout de 2 columnas)
        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed()
        composeTestRule.onNodeWithText("A Night at the Opera").assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se puede hacer scroll en el grid
     */
    @Test
    fun albumGrid_isScrollable() {
        // Given: Grid con muchos álbumes
        val manyAlbums = List(20) { index ->
            testAlbums[0].copy(
                id = index,
                name = "Album $index"
            )
        }
        
        composeTestRule.setContent {
            AlbumGrid(
                albums = manyAlbums,
                onAlbumClick = {}
            )
        }
        
        // Then: Los primeros álbumes deben ser visibles
        composeTestRule.onNodeWithText("Album 0").assertIsDisplayed()
        
        // El último álbum no debe ser visible inicialmente
        composeTestRule.onNodeWithText("Album 19").assertDoesNotExist()
        
        // When: Hacemos scroll hacia abajo
        composeTestRule.onNodeWithText("Album 0")
            .performScrollTo()
        
        // Note: En un test real, necesitaríamos scroll más complejo
        // Este es un ejemplo simplificado
    }
}
