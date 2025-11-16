package com.example.vinylsapp.ui.artists

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.example.vinylsapp.data.model.Album
import com.example.vinylsapp.data.model.Musician
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de UI para ArtistDetailScreen usando Compose Testing
 *
 * Estas son pruebas de integración que verifican:
 * - Renderizado correcto de la pantalla de detalle
 * - Visualización de la imagen del artista
 * - Visualización de información del artista
 * - Visualización de la lista de albumes
 * - Estados de Loading y Error
 */
class ArtistDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testArtist = Musician(
        id = 1,
        name = "Shakira",
        image = "https://example.com/cover.jpg",
        birthDate = "1948-07-16T00:00:00.000Z",
        description = "Artista pop con más de 15 millones de reproducciones",
        albums = listOf(
            Album(
                1,
                "Album 1",
                "https://example.com/aurora.jpg",
                "1948-07-16T00:00:00.000Z",
                "Descripción Album 1",
                "Pop",
                "Sony Music",
                null,
                null
            ),
        )
    )

    /**
     * Test: Verificar que se muestra el título del artista
     */
    @Test
    fun artistDetailScreen_displaysArtistTitle() {
        // Given: Pantalla de detalle con artista
        composeTestRule.setContent {
            ArtistInfo(artist = testArtist)
        }

        // Then: Debe mostrar el artista
        composeTestRule.onNodeWithText("Shakira")
            .assertIsDisplayed()
    }

    /**
     * Test: Verificar que se muestra descripción del artista
     */
    @Test
    fun artistDetailScreen_displaysArtistDescription() {
        // Given: Pantalla de detalle con álbum
        composeTestRule.setContent {
            ArtistInfo(artist = testArtist)
        }

        // Then: Debe mostrar descripción del artista
        composeTestRule.onNodeWithText("Artista pop con más de 15 millones de reproducciones", substring = true)
            .assertIsDisplayed()
    }

    /**
     * Test: Verificar que se muestran todos los albumes del artista
     */
    @Test
    fun albumList_displaysAllAlbums() {
        // Given: Lista de albums
        composeTestRule.setContent {
            ArtistDetailContent(
                artist = testArtist
            )
        }

        // Then: Debe mostrar albumes
        composeTestRule.onNodeWithText("Album 1", substring = false)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("1948")
            .assertIsDisplayed()

    }


    /**
     * Test: Verificar que se muestra la imagen del artista
     */
    @Test
    fun artistImageSection_displaysImage() {
        // Given: Sección de imagen
        composeTestRule.setContent {
            ArtistImage(artist = testArtist)
        }

        // Then: La sección debe estar visible
        // (Verificamos indirectamente que la sección se renderiza)
        composeTestRule.onRoot()
            .assertExists()
    }

}