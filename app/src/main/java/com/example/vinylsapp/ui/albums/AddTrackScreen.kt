package com.example.vinylsapp.ui.albums

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vinylsapp.R
import com.example.vinylsapp.ui.theme.VinylPurple

/**
 * Pantalla para agregar un nuevo track a un álbum
 * Muestra un formulario con todos los campos requeridos y opcionales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTrackScreen(
    viewModel: AddTrackViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTrackCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Mostrar mensaje de éxito y navegar de vuelta
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onTrackCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_track_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar error si existe
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Campo: Nombre del track (obligatorio)
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { 
                    Text(
                        text = stringResource(R.string.track_name),
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = VinylPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = VinylPurple,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                supportingText = {
                    Text(
                        text = stringResource(R.string.track_name_required),
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
            
            // Campo: Duración (opcional)
            OutlinedTextField(
                value = uiState.duration,
                onValueChange = viewModel::updateDuration,
                label = { 
                    Text(
                        text = stringResource(R.string.track_duration),
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
                placeholder = {
                    Text(
                        text = "03:45",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = VinylPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = VinylPurple,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                supportingText = {
                    Text(
                        text = stringResource(R.string.track_duration_hint),
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
            
            // Campo: Número de pista (opcional)
            OutlinedTextField(
                value = uiState.number,
                onValueChange = { newValue ->
                    // Solo permitir números
                    if (newValue.all { it.isDigit() }) {
                        viewModel.updateNumber(newValue)
                    }
                },
                label = { 
                    Text(
                        text = stringResource(R.string.track_number),
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
                placeholder = {
                    Text(
                        text = "1",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = VinylPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = VinylPurple,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )
            
            // Campo: Compositor o artista colaborador (opcional)
            OutlinedTextField(
                value = uiState.composer,
                onValueChange = viewModel::updateComposer,
                label = { 
                    Text(
                        text = stringResource(R.string.track_composer),
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = VinylPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = VinylPurple,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                
                // Botón Guardar
                Button(
                    onClick = {
                        viewModel.createTrack(onSuccess = {})
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && uiState.isValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VinylPurple
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (uiState.isLoading) {
                            stringResource(R.string.saving)
                        } else {
                            stringResource(R.string.save_track)
                        }
                    )
                }
            }
        }
    }
}

