package com.example.vinylsapp.ui.collectors

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.vinylsapp.data.model.Collector
import com.example.vinylsapp.data.model.FeaturedAlbum
import com.example.vinylsapp.ui.theme.VinylsAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CollectorDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun collectorDetailContent_displaysDataAndHandlesAlbumClick() {
        val featuredAlbum = FeaturedAlbum(
            id = 99,
            title = "Test Album",
            artist = "Test Artist",
            coverUrl = "https://i.imgur.com/7b1KxVI.jpeg"
        )
        val collector = Collector(
            id = 10,
            name = "UI Tester",
            city = "Bogotá",
            country = "Colombia",
            shortBio = "Bio corta",
            totalAlbums = 5,
            favoriteGenres = listOf("Rock", "Jazz"),
            favoriteArtists = listOf("Artist 1", "Artist 2"),
            featuredAlbums = listOf(featuredAlbum)
        )

        var clickedAlbumId = -1

        composeTestRule.setContent {
            VinylsAppTheme {
                CollectorDetailContent(
                    collector = collector,
                    onAlbumClick = { clickedAlbumId = it }
                )
            }
        }

        composeTestRule.onNodeWithText("UI Tester").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bogotá, Colombia").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Album").performClick()

        assertEquals(99, clickedAlbumId)
    }
}
