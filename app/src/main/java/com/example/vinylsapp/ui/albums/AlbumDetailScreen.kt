package com.example.vinylsapp.ui.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.vinylsapp.R
import com.example.vinylsapp.data.model.Track
import com.example.vinylsapp.ui.theme.VinylPurple
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de detalle de álbum
 * Implementa todos los criterios de aceptación de HU02
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onAddTrackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.album_detail_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1B2E), // Fondo oscuro morado
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF1E1B2E) // Fondo oscuro para toda la pantalla
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Estado de carga
                uiState.isLoading -> {
                    AlbumDetailLoadingIndicator()
                }
                
                // Estado de error
                uiState.error != null -> {
                    AlbumDetailErrorMessage(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadAlbumDetails() }
                    )
                }
                
                // Mostrar detalles del álbum
                uiState.album != null -> {
                    AlbumDetailContent(
                        album = uiState.album!!,
                        selectedTrackId = uiState.selectedTrackId,
                        onTrackClick = { trackId ->
                            viewModel.selectTrack(trackId)
                        },
                        onAddTrackClick = onAddTrackClick
                    )
                }
            }
        }
    }
}

/**
 * Contenido principal de la pantalla de detalles
 */
@Composable
fun AlbumDetailContent(
    album: com.example.vinylsapp.data.model.Album,
    selectedTrackId: Int?,
    onTrackClick: (Int) -> Unit,
    onAddTrackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Espacio para la barra de navegación
    ) {
        // Sección superior con portada del álbum
        item {
            AlbumCoverSection(coverUrl = album.cover)
        }
        
        // Información del álbum
        item {
            AlbumInfoSection(album = album)
        }
        
        // Sección de canciones
        item {
            SongsSectionHeader(onAddTrackClick = onAddTrackClick)
        }
        
        // Lista de canciones
        itemsIndexed(
            items = album.tracks ?: emptyList(),
            key = { _, track -> track.id }
        ) { index, track ->
            TrackItem(
                track = track,
                trackNumber = index + 1,
                isSelected = track.id == selectedTrackId,
                onClick = { onTrackClick(track.id) }
            )
        }
    }
}

/**
 * Sección superior con la portada del álbum en tamaño grande
 * Fondo claro (peachy-pink) según el diseño
 */
@Composable
fun AlbumCoverSection(coverUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFFFE5D9)) // Fondo peachy-pink claro
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Portada con marco
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD4A574)) // Color del marco (marrón claro)
                .padding(8.dp)
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Portada del álbum",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = VinylPurple
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "No image",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                    }
                }
            )
        }
    }
}

/**
 * Sección de información del álbum
 * Título, artista, año, género y descripción
 */
@Composable
fun AlbumInfoSection(album: com.example.vinylsapp.data.model.Album) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1B2E)) // Fondo oscuro
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Título del álbum
        Text(
            text = album.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Nombre del artista
        Text(
            text = "Por ${album.performers?.firstOrNull()?.name ?: "Artista desconocido"}",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF9F67FF), // Morado claro
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Año y género
        val releaseYear = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(album.releaseDate)
            SimpleDateFormat("yyyy", Locale.getDefault()).format(date ?: Date())
        } catch (e: Exception) {
            album.releaseDate.take(4) // Intentar extraer año del string
        }
        
        Text(
            text = "$releaseYear · ${album.genre}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descripción del álbum
        Text(
            text = album.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Encabezado de la sección de canciones con botón "+"
 */
@Composable
fun SongsSectionHeader(onAddTrackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1B2E))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.songs),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        // Botón "+" para agregar canciones
        IconButton(
            onClick = onAddTrackClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    VinylPurple,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_track),
                tint = Color.White
            )
        }
    }
}

/**
 * Item individual de una canción en la lista
 * Se resalta con fondo morado cuando está seleccionada
 */
@Composable
fun TrackItem(
    track: Track,
    trackNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) VinylPurple else Color.Transparent,
                RoundedCornerShape(0.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de pista
        Text(
            text = "$trackNumber",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.width(32.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Título de la canción
        Text(
            text = track.name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Duración
        Text(
            text = track.duration,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

/**
 * Indicador de carga centrado para la pantalla de detalle
 */
@Composable
private fun AlbumDetailLoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = VinylPurple
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

/**
 * Mensaje de error con botón de reintentar para la pantalla de detalle
 */
@Composable
private fun AlbumDetailErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VinylPurple
                )
            ) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

