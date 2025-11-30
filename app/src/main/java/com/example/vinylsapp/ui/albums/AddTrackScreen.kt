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
        topBar = { AddTrackTopBar(onNavigateBack) },
        containerColor = Color(0xFF1E1B2E) // Fondo oscuro para toda la pantalla
    ) { paddingValues ->
        AddTrackContent(
            uiState = uiState,
            paddingValues = paddingValues,
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTrackTopBar(onNavigateBack: () -> Unit) {
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
}

@Composable
private fun AddTrackContent(
    uiState: AddTrackUiState,
    paddingValues: PaddingValues,
    viewModel: AddTrackViewModel,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ErrorMessage(uiState.error)
        
        TrackNameField(
            value = uiState.name,
            isLoading = uiState.isLoading,
            onValueChange = viewModel::updateName
        )
        
        TrackDurationField(
            value = uiState.duration,
            isLoading = uiState.isLoading,
            onValueChange = viewModel::updateDuration
        )
        
        TrackNumberField(
            value = uiState.number,
            isLoading = uiState.isLoading,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    viewModel.updateNumber(newValue)
                }
            }
        )
        
        TrackComposerField(
            value = uiState.composer,
            isLoading = uiState.isLoading,
            onValueChange = viewModel::updateComposer
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ActionButtons(
            isLoading = uiState.isLoading,
            isValid = uiState.isValid(),
            onNavigateBack = onNavigateBack,
            onSave = { viewModel.createTrack(onSuccess = {}) }
        )
    }
}

@Composable
private fun ErrorMessage(error: String?) {
    error?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = it,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun TrackNameField(
    value: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = stringResource(R.string.track_name),
                color = Color.White.copy(alpha = 0.7f)
            ) 
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        colors = getTextFieldColors(),
        supportingText = {
            Text(
                text = stringResource(R.string.track_name_required),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}

@Composable
private fun TrackDurationField(
    value: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = stringResource(R.string.track_duration),
                color = Color.White.copy(alpha = 0.7f)
            ) 
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        placeholder = {
            Text(
                text = "03:45",
                color = Color.White.copy(alpha = 0.5f)
            )
        },
        colors = getTextFieldColors(),
        supportingText = {
            Text(
                text = stringResource(R.string.track_duration_hint),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}

@Composable
private fun TrackNumberField(
    value: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = stringResource(R.string.track_number),
                color = Color.White.copy(alpha = 0.7f)
            ) 
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        placeholder = {
            Text(
                text = "1",
                color = Color.White.copy(alpha = 0.5f)
            )
        },
        colors = getTextFieldColors()
    )
}

@Composable
private fun TrackComposerField(
    value: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = stringResource(R.string.track_composer),
                color = Color.White.copy(alpha = 0.7f)
            ) 
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        colors = getTextFieldColors()
    )
}

@Composable
private fun ActionButtons(
    isLoading: Boolean,
    isValid: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón Cancelar
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(R.string.cancel))
        }
        
        // Botón Guardar
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = !isLoading && isValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = VinylPurple
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isLoading) {
                    stringResource(R.string.saving)
                } else {
                    stringResource(R.string.save_track)
                }
            )
        }
    }
}

@Composable
private fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = VinylPurple,
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    focusedLabelColor = VinylPurple,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
)
