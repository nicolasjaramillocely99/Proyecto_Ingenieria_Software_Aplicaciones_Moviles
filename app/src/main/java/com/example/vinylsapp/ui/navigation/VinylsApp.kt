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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.vinylsapp.ui.albums.AlbumListScreen
import com.example.vinylsapp.ui.albums.AlbumViewModel
import com.example.vinylsapp.ui.albums.AlbumDetailScreen
import com.example.vinylsapp.ui.albums.AlbumDetailViewModel
import com.example.vinylsapp.ui.albums.CreateAlbumScreen
import com.example.vinylsapp.ui.albums.AddTrackScreen
import com.example.vinylsapp.ui.albums.AddTrackViewModel
import com.example.vinylsapp.ui.artists.ArtistListScreen
import com.example.vinylsapp.ui.collectors.CollectorListScreen
import com.example.vinylsapp.ui.artists.ArtistDetailScreen


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
            composable(Screen.Albums.route) { backStackEntry ->
                val viewModel: AlbumViewModel = hiltViewModel()

                // Recargar álbumes si se creó uno nuevo
                androidx.compose.runtime.LaunchedEffect(
                    backStackEntry.savedStateHandle["album_created"]
                ) {
                    if (backStackEntry.savedStateHandle["album_created"] == true) {
                        viewModel.loadAlbums()
                        backStackEntry.savedStateHandle.remove<Boolean>("album_created")
                    }
                }

                AlbumListScreen(
                    viewModel = viewModel,
                    onAlbumClick = { albumId ->
                        navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                    },
                    onAddAlbumClick = {
                        navController.navigate(Screen.CreateAlbum.route)
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

            // Pantalla de coleccionistas
            composable(Screen.Collectors.route) {
                CollectorListScreen()
            }

            // Pantalla de mi colección (placeholder)
            composable(Screen.MyCollection.route) {
                PlaceholderScreen(title = Screen.MyCollection.title)
            }
            
            // Pantalla de detalle de álbum
            composable(
                route = Screen.AlbumDetail.route,
                arguments = listOf(
                    navArgument("albumId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                // Hilt automáticamente inyecta el SavedStateHandle con los argumentos
                val viewModel: AlbumDetailViewModel = hiltViewModel(backStackEntry)

                AlbumDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAddTrackClick = {
                        val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
                        navController.navigate(Screen.AddTrack.createRoute(albumId))
                    }
                )
            }

            // Pantalla de detalle de artistas
            composable(
                route = Screen.ArtistDetail.route,
                arguments = listOf(
                    navArgument("artistId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val artistId = backStackEntry.arguments?.getInt("artistId") ?: 0
                ArtistDetailScreen(
                    artistId = artistId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Pantalla de crear álbum
            composable(Screen.CreateAlbum.route) {
                CreateAlbumScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAlbumCreated = {
                        // Obtener el backStackEntry de Albums y marcar que se creó un álbum
                        navController.getBackStackEntry(Screen.Albums.route)
                            .savedStateHandle["album_created"] = true
                        navController.popBackStack()
                    }
                )
            }
            
            // Pantalla de agregar track
            composable(
                route = Screen.AddTrack.route,
                arguments = listOf(
                    navArgument("albumId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val viewModel: AddTrackViewModel = hiltViewModel(backStackEntry)
                val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
                
                AddTrackScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTrackCreated = {
                        // Marcar que se creó un track para refrescar el detalle del álbum
                        // Usar previousBackStackEntry que debería ser AlbumDetail
                        navController.previousBackStackEntry?.savedStateHandle?.set("track_created", true)
                        navController.popBackStack()
                    }
                )
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
