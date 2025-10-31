package com.example.vinylsapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vinylsapp.ui.albums.AlbumListScreen
import com.example.vinylsapp.ui.artists.ArtistListScreen

/**
 * Composable principal que gestiona la navegación de la aplicación
 * Incluye la barra de navegación inferior según el diseño
 */
@Composable
fun VinylsApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Albums.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantalla de álbumes
            composable(Screen.Albums.route) {
                AlbumListScreen(
                    onAlbumClick = { albumId ->
                        navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                    },
                    onAddAlbumClick = {
                        // TODO: Navegar a pantalla de agregar álbum
                    }
                )
            }
            
            // Pantalla de artistas (placeholder)
            composable(Screen.Artists.route) {
                ArtistListScreen(
                    onArtistClick = { artistId ->
                        navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                    }
                )
            }
            
            // Pantalla de coleccionistas (placeholder)
            composable(Screen.Collectors.route) {
                PlaceholderScreen(title = Screen.Collectors.title)
            }
            
            // Pantalla de mi colección (placeholder)
            composable(Screen.MyCollection.route) {
                PlaceholderScreen(title = Screen.MyCollection.title)
            }
            
            // Pantalla de detalle de álbum (placeholder)
            composable(Screen.AlbumDetail.route) {
                PlaceholderScreen(title = Screen.AlbumDetail.title)
            }
        }
    }
}

/**
 * Barra de navegación inferior con las 4 opciones principales
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        bottomNavScreens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop toda la pila hasta el destino de inicio (Albums)
                        popUpTo(Screen.Albums.route) {
                            // Si ya estamos en Albums, no hacer nada más
                            inclusive = screen.route != Screen.Albums.route
                        }
                        // Evitar múltiples copias de la misma pantalla
                        launchSingleTop = true
                        // Restaurar el estado cuando se vuelve a seleccionar
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Pantalla placeholder para las secciones no implementadas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de $title (Por implementar)")
        }
    }
}
