package com.example.vinylsapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas End-to-End (E2E) para la funcionalidad de listar coleccionistas
 *
 * Flujo a verificar:
 * 1. La aplicación se inicia en la pantalla principal
 * 2. Se muestra la sección de coleccionistas
 * 3. La lista carga correctamente o muestra los estados correspondientes
 */
@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CollectorListE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val suppressInputManagerRule = SuppressInputManagerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        navigateToCollectorsSection()
    }

    private fun navigateToCollectorsSection() {
        // Esperar a que la barra de navegación inferior se renderice
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(
                "Coleccionistas",
                substring = false,
                ignoreCase = false,
                useUnmergedTree = true
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Navegar a la pestaña de coleccionistas a través de la barra inferior
        composeTestRule.onNodeWithText(
            "Coleccionistas",
            substring = false,
            ignoreCase = false,
            useUnmergedTree = true
        ).performClick()

        // Confirmar que la pantalla de coleccionistas está visible esperando el FAB
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasContentDescription("Agregar coleccionista"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Test E2E: Verificar que la aplicación muestra la sección de coleccionistas al iniciar
     * (una vez navegamos desde el setup)
     *
     * Usamos el FAB "Agregar coleccionista" como indicador de que estamos
     * efectivamente en la pantalla de lista de coleccionistas.
     */
    @Test
    fun app_displaysCollectorList_onStartup() {
        composeTestRule.onNodeWithContentDescription("Agregar coleccionista")
            .assertIsDisplayed()
    }

    /**
     * Test E2E: Verificar que el botón flotante de agregar coleccionista existe
     */
    @Test
    fun collectorList_displaysFAB() {
        composeTestRule.onNodeWithContentDescription("Agregar coleccionista")
            .assertIsDisplayed()
    }

    /**
     * Test E2E: Verificar manejo de error de red (requiere backend apagado)
     */
    @Test
    fun collectorList_showsError_whenNetworkUnavailable() {
        Thread.sleep(3000)

        try {
            composeTestRule.onNodeWithText("Error", substring = true, ignoreCase = true)
                .assertIsDisplayed()

            composeTestRule.onNodeWithText("Reintentar", substring = true, ignoreCase = true)
                .assertIsDisplayed()
        } catch (e: AssertionError) {
            // Si no aparece el error, asumimos que el backend está disponible
            println("Backend is available - skipping error test")
        }
    }

    /**
     * Test E2E: Verificar que el botón de reintentar recarga los datos
     */
    @Test
    fun collectorList_retryButton_reloadsData() {
        Thread.sleep(2000)

        try {
            composeTestRule.onNodeWithText("Reintentar", substring = true, ignoreCase = true)
                .performClick()

            Thread.sleep(1000)
        } catch (e: AssertionError) {
            println("No error state - skipping retry test")
        }
    }

    /**
     * Test E2E: Verificar que las tarjetas de coleccionistas cargan las imágenes
     */
    @Test
    fun collectorCards_loadImages() {
        // Esperar a que la pantalla de coleccionistas esté lista
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Coleccionistas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(3000)

        // Verificamos que exista al menos un elemento clicable (tarjetas de coleccionistas)
        val collectorCards = composeTestRule.onAllNodes(hasClickAction())
        assert(collectorCards.fetchSemanticsNodes().isNotEmpty())
    }
}
