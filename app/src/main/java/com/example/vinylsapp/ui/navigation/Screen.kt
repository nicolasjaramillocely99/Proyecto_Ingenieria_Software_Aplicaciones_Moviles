package com.example.vinylsapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Destinos de navegación de la aplicación
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Albums : Screen(
        route = "albums",
        title = "Álbumes",
        icon = Icons.Default.Album
    )
    
    object Artists : Screen(
        route = "artists",
        title = "Artistas",
        icon = Icons.Default.Person
    )
    
    object Collectors : Screen(
        route = "collectors",
        title = "Coleccionistas",
        icon = Icons.Default.Group
    )
    
    object MyCollection : Screen(
        route = "my_collection",
        title = "Mi Colección",
        icon = Icons.Default.LibraryMusic
    )
    
    object AlbumDetail : Screen(
        route = "album/{albumId}",
        title = "Detalle del Álbum",
        icon = Icons.Default.Album
    ) {
        fun createRoute(albumId: Int) = "album/$albumId"
    }

    object ArtistDetail : Screen(
        route = "artists/{artistId}",
        title = "Detalle del artista",
        icon = Icons.Default.Person
    ) {
        fun createRoute(artistId: Int) = "artists/$artistId"
    }
}

/**
 * Lista de pantallas que aparecen en la barra de navegación inferior
 */
val bottomNavScreens = listOf(
    Screen.Albums,
    Screen.Artists,
    Screen.Collectors,
    Screen.MyCollection
)
