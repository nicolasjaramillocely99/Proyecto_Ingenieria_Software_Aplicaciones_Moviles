package com.example.vinylsapp.ui.artists

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.vinylsapp.data.model.Musician
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de UI para ArtistListScreen usando Compose Testing
 *
 * Estas son pruebas de integración que verifican:
 * - Renderizado correcto de la pantalla
 * - Visualización de artistas
 * - Interacciones del usuario (clicks, scroll)
 * - Estados de Loading, Error y Empty
 */
class ArtistListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testArtist = listOf(
        Musician(
            id = 100,
            name = "Rubén Blades Bellido de Luna",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
            description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
            birthDate = "1948-07-16T00:00:00.000Z"
        ),
        Musician(
            id = 2,
            name = "Celia Cruz",
            image = "https://upload.wikimedia.org/wikipedia/commons/d/d8/Celia_Cruz%2C_1957_%28cropped%29.jpg",
            description = "Fue una cantante cubana de música tropical. Apodada «la Reina de la salsa»​ y «la Guarachera de Cuba»,​ es ampliamente considerada como una de las artistas latinas más populares e importantes del siglo XX y un icono de la música latina.",
            birthDate = "1948-07-16T05:00:00.000Z"
        ),
        Musician(
            id = 5,
            name = "Shakira Isabel Mebarak Ripoll",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/2023-11-16_Gala_de_los_Latin_Grammy%2C_03_%28cropped%2901.jpg/330px-2023-11-16_Gala_de_los_Latin_Grammy%2C_03_%28cropped%2901.jpg",
            description = "Es una cantante, compositora, productora, bailarina, multinstrumentista, empresaria y actriz colombiana.",
            birthDate = "1948-07-16T05:00:00.000Z"
        )
    )

    /**
     * Test: Verificar que el grid muestra los artistas correctamente
     */
    @Test
    fun artistGrid_displaysArtists_correctly() {
        composeTestRule.setContent {
            ArtistGrid(
                artists = testArtist,
                onArtistClick = {}
            )
        }

        composeTestRule.onNodeWithText("Rubén Blades Bellido de Luna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Celia Cruz").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shakira Isabel Mebarak Ripoll").assertIsDisplayed()
    }

    /**
     * Test: Verificar que se muestran los nombres de los artistas
     */
    @Test
    fun artistCard_displaysPerformerName() {
        composeTestRule.setContent {
            ArtistCard (
                artist = testArtist[0],
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Rubén Blades Bellido de Luna").assertIsDisplayed()
    }


    /**
     * Test: Verificar que el click en un artista dispara el callback
     */
    @Test
    fun artistCard_onClick_triggersCallback() {
        var clickedArtistId: Int? = null

        composeTestRule.setContent {
            ArtistCard(
                artist = testArtist[0],
                onClick = { clickedArtistId = testArtist[0].id }
            )
        }

        composeTestRule.onNodeWithText("Rubén Blades Bellido de Luna").performClick()

        assert(clickedArtistId == 100)
    }

    /**
     * Test: Verificar que se muestra el indicador de carga
     */
    @Test
    fun loadingIndicator_isDisplayed() {
        composeTestRule.setContent {
            LoadingIndicator()
        }

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
        composeTestRule.onNodeWithText("No hay artistas disponibles", substring = true)
            .assertExists()
    }


    /**
     * Test: Verificar que se puede hacer scroll en el grid
     */
    @Test
    fun artistGrid_isScrollable() {
        // Given: Grid con muchos artistas
        val manyArtist = List(20) { index ->
            testArtist[0].copy(
                id = index,
                name = "Artist $index"
            )
        }

        composeTestRule.setContent {
            ArtistGrid (
                artists = manyArtist,
                onArtistClick = {}
            )
        }

        // Then: Los primeros artistas deben ser visibles
        composeTestRule.onNodeWithText("Artist 0").assertIsDisplayed()

        // El último artista no debe ser visible inicialmente
        composeTestRule.onNodeWithText("Artist 19").assertDoesNotExist()

        // When: Hacemos scroll hacia abajo
        composeTestRule.onNodeWithText("Artist 0")
            .performScrollTo()

    }
}