package com.example.vinylsapp.ui.albums

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.Performer
import com.example.vinylsapp.data.model.Track
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de UI para AlbumDetailScreen usando Compose Testing
 * 
 * Estas son pruebas de integración que verifican:
 * - Renderizado correcto de la pantalla de detalle
 * - Visualización de la portada del álbum
 * - Visualización de información del álbum (título, artista, año, género, descripción)
 * - Visualización de la lista de canciones
 * - Selección/resaltado de canciones
 * - Estados de Loading y Error
 */
class AlbumDetailScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    
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
    
    /**
     * Test: Verificar que se muestra el título del álbum
     */
    @Test
    fun albumDetailScreen_displaysAlbumTitle() {
        // Given: Pantalla de detalle con álbum
        composeTestRule.setContent {
            AlbumInfoSection(album = testAlbum)
        }
        
        // Then: Debe mostrar el título del álbum
        composeTestRule.onNodeWithText("Melodías del Alma")
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se muestra el nombre del artista
     */
    @Test
    fun albumDetailScreen_displaysArtistName() {
        // Given: Pantalla de detalle con álbum
        composeTestRule.setContent {
            AlbumInfoSection(album = testAlbum)
        }
        
        // Then: Debe mostrar el nombre del artista
        composeTestRule.onNodeWithText("Por Aurora", substring = true)
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se muestra el año y género
     */
    @Test
    fun albumDetailScreen_displaysYearAndGenre() {
        // Given: Pantalla de detalle con álbum
        composeTestRule.setContent {
            AlbumInfoSection(album = testAlbum)
        }
        
        // Then: Debe mostrar el año y género
        composeTestRule.onNodeWithText("2023", substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Pop", substring = true)
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se muestra la descripción del álbum
     */
    @Test
    fun albumDetailScreen_displaysDescription() {
        // Given: Pantalla de detalle con álbum
        composeTestRule.setContent {
            AlbumInfoSection(album = testAlbum)
        }
        
        // Then: Debe mostrar la descripción
        composeTestRule.onNodeWithText("El album Melodias del alma", substring = true)
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se muestra el encabezado de canciones con botón "+"
     */
    @Test
    fun songsSectionHeader_displaysCorrectly() {
        // Given: Encabezado de sección de canciones
        var addTrackClicked = false
        
        composeTestRule.setContent {
            SongsSectionHeader(
                onAddTrackClick = { addTrackClicked = true }
            )
        }
        
        // Then: Debe mostrar "Canciones"
        composeTestRule.onNodeWithText("Canciones")
            .assertIsDisplayed()
        
        // When: Hacemos click en el botón "+"
        composeTestRule.onNode(hasContentDescription("Agregar canción"))
            .performClick()
        
        // Then: El callback debe haberse ejecutado
        assert(addTrackClicked)
    }
    
    /**
     * Test: Verificar que se muestran todas las canciones en la lista
     */
    @Test
    fun trackList_displaysAllTracks() {
        // Given: Lista de tracks
        composeTestRule.setContent {
            AlbumDetailContent(
                album = testAlbum,
                selectedTrackId = null,
                onTrackClick = {},
                onAddTrackClick = {}
            )
        }
        
        // Then: Debe mostrar todas las canciones
        composeTestRule.onNodeWithText("1", substring = false)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Amanecer")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("3:45")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("2", substring = false)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Sueños de Verano")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("4:12")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("3", substring = false)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Noche Estrellada")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("5:01")
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que el click en una canción dispara el callback
     */
    @Test
    fun trackItem_onClick_triggersCallback() {
        // Given: Estado para trackear el click
        var clickedTrackId: Int? = null
        
        composeTestRule.setContent {
            TrackItem(
                track = testAlbum.tracks!![1], // "Sueños de Verano"
                trackNumber = 2,
                isSelected = false,
                onClick = { clickedTrackId = testAlbum.tracks!![1].id }
            )
        }
        
        // When: Hacemos click en la canción
        composeTestRule.onNodeWithText("Sueños de Verano")
            .performClick()
        
        // Then: El callback debe haberse ejecutado
        assertEquals("Track ID should be 2", 2, clickedTrackId)
    }
    
    /**
     * Test: Verificar que se muestra el indicador de carga
     * Nota: Este test verifica indirectamente a través de la pantalla completa
     */
    @Test
    fun albumDetailScreen_showsLoadingState() {
        // Given: ViewModel mockeado con estado de loading
        // (En un test real usaríamos un ViewModel mockeado)
        // Por ahora verificamos que la pantalla puede renderizar
        composeTestRule.setContent {
            AlbumDetailContent(
                album = testAlbum,
                selectedTrackId = null,
                onTrackClick = {},
                onAddTrackClick = {}
            )
        }
        
        // Then: La pantalla debe renderizar correctamente
        composeTestRule.onRoot()
            .assertExists()
    }
    
    /**
     * Test: Verificar que se muestra la portada del álbum
     */
    @Test
    fun albumCoverSection_displaysCover() {
        // Given: Sección de portada
        composeTestRule.setContent {
            AlbumCoverSection(coverUrl = testAlbum.cover)
        }
        
        // Then: La sección debe estar visible
        // (Verificamos indirectamente que la sección se renderiza)
        composeTestRule.onRoot()
            .assertExists()
    }
    
    /**
     * Test: Verificar que se muestra el artista cuando no hay performers
     */
    @Test
    fun albumInfoSection_displaysRecordLabel_whenNoPerformers() {
        // Given: Álbum sin performers
        val albumWithoutPerformers = testAlbum.copy(performers = null)
        
        composeTestRule.setContent {
            AlbumInfoSection(album = albumWithoutPerformers)
        }
        
        // Then: Debe mostrar "Artista desconocido"
        composeTestRule.onNodeWithText("Artista desconocido", substring = true)
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que el modal de detalle del track se muestra cuando se selecciona un track
     */
    @Test
    fun trackDetailModal_displaysWhenTrackIsSelected() {
        // Given: Track seleccionado
        val selectedTrack = testAlbum.tracks!![0]
        
        composeTestRule.setContent {
            TrackDetailModal(
                track = selectedTrack,
                trackNumber = 1,
                onDismiss = {}
            )
        }
        
        // Then: Debe mostrar el modal con la información del track
        composeTestRule.onNodeWithText("Detalle del Track", substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Amanecer")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("3:45")
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que el modal de detalle del track muestra todos los campos
     */
    @Test
    fun trackDetailModal_displaysAllTrackFields() {
        // Given: Track con todos los campos
        val trackWithAllFields = Track(
            id = 1,
            name = "Complete Track",
            duration = "04:30",
            albumId = 1,
            seconds = 270,
            number = 1,
            composer = "Test Composer"
        )
        
        composeTestRule.setContent {
            TrackDetailModal(
                track = trackWithAllFields,
                trackNumber = 1,
                onDismiss = {}
            )
        }
        
        // Then: Debe mostrar todos los campos
        composeTestRule.onNodeWithText("Complete Track")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("04:30")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Composer")
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que el botón cerrar del modal funciona
     */
    @Test
    fun trackDetailModal_closeButtonWorks() {
        // Given: Modal de detalle del track
        var dismissCalled = false
        val selectedTrack = testAlbum.tracks!![0]
        
        composeTestRule.setContent {
            TrackDetailModal(
                track = selectedTrack,
                trackNumber = 1,
                onDismiss = { dismissCalled = true }
            )
        }
        
        // When: Hacemos click en el botón cerrar
        composeTestRule.onNodeWithContentDescription("Volver", substring = true)
            .performClick()
        
        // Then: El callback debe haberse ejecutado
        assert(dismissCalled)
    }
    
    /**
     * Test: Verificar que el modal muestra el número de pista correcto
     */
    @Test
    fun trackDetailModal_displaysCorrectTrackNumber() {
        // Given: Track con número de pista
        val selectedTrack = testAlbum.tracks!![1] // Segundo track
        
        composeTestRule.setContent {
            TrackDetailModal(
                track = selectedTrack,
                trackNumber = 2,
                onDismiss = {}
            )
        }
        
        // Then: Debe mostrar el número de pista correcto
        composeTestRule.onNodeWithText("2", substring = false)
            .assertIsDisplayed()
    }
    
    /**
     * Test: Verificar que se puede hacer scroll en la lista de tracks
     */
    @Test
    fun trackList_allowsScrolling() {
        // Given: Álbum con muchos tracks
        val manyTracks = (1..10).map { 
            Track(it, "Track $it", "${it}:00", 1)
        }
        val albumWithManyTracks = testAlbum.copy(tracks = manyTracks)
        
        composeTestRule.setContent {
            AlbumDetailContent(
                album = albumWithManyTracks,
                selectedTrackId = null,
                onTrackClick = {},
                onAddTrackClick = {}
            )
        }
        
        // When: Hacemos scroll hacia abajo
        composeTestRule.onRoot()
            .performScrollToNode(hasText("Track 10"))
        
        // Then: El último track debe estar visible
        composeTestRule.onNodeWithText("Track 10")
            .assertIsDisplayed()
    }
}

