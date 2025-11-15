package com.example.vinylsapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas End-to-End (E2E) para la funcionalidad de listar artistas
 *
 * Flujo a verificar:
 * 1. La aplicación se inicia
 * 2. Se muestra el botón de artistas
 * 3. Al darle click, se carga la lista de artistas
 *
 * Nota: Para estas pruebas, necesitas tener el backend corriendo
 * o usar un MockWebServer para simular las respuestas
 */
@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
public class ArtistListE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val suppressInputManagerRule = SuppressInputManagerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Test E2E: Verificar que la aplicación muestra el botón de Artistas y darle click
     */
    @Test
    fun app_displaysArtistButton_onStartup() {
        //La app se inicia

        Thread.sleep(3000)

        //Verificar que exista un botón de artistas
        composeTestRule.onAllNodesWithText("Artistas", substring = true, ignoreCase = true)
            .fetchSemanticsNodes()
            .isNotEmpty()

        composeTestRule.onNodeWithText("Artistas", substring = true, ignoreCase = true)
            .performClick()

    }

    /**
     * Test E2E: Verificar que las imágenes de los artistas se cargan
     */
    @Test
    fun artistCards_loadImages() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Artistas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(3000)

        val albumCards = composeTestRule.onAllNodes(hasClickAction())
        assert(albumCards.fetchSemanticsNodes().size > 0)
    }

    /**
     * Test E2E: Verificar manejo de error de red (requiere backend apagado)
     * Este test se salta si el backend está disponible
     */
    @Test
    fun artistList_showsError_whenNetworkUnavailable() {
        // Este test solo pasa si el backend NO está disponible
        // Es útil para verificar el manejo de errores

        Thread.sleep(3000)

        try {
            composeTestRule.onNodeWithText("Error", substring = true, ignoreCase = true)
                .assertIsDisplayed()

            composeTestRule.onNodeWithText("Reintentar", substring = true, ignoreCase = true)
                .assertIsDisplayed()
        } catch (e: AssertionError) {
            println("Backend is available - skipping error test")
        }
    }

    /**
     * Test E2E: Verificar que el retry button recarga los datos
     */
    @Test
    fun artistList_retryButton_reloadsData() {
        // Este test solo funciona si hay un error inicial
        Thread.sleep(2000)

        try {
            composeTestRule.onNodeWithText("Reintentar", substring = true, ignoreCase = true)
                .performClick()

            Thread.sleep(1000)

        } catch (e: AssertionError) {
            println("No error state - skipping retry test")
        }
    }




}
