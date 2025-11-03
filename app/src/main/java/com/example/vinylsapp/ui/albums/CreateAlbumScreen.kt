package com.example.vinylsapp.ui.albums

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vinylsapp.R

/**
 * Pantalla para crear un nuevo álbum
 * Muestra un formulario con todos los campos requeridos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumScreen(
    viewModel: CreateAlbumViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAlbumCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_album_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
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
            
            // Campo: Nombre del álbum
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.album_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            
            // Campo: URL de la portada
            OutlinedTextField(
                value = uiState.cover,
                onValueChange = viewModel::updateCover,
                label = { Text(stringResource(R.string.album_cover_url)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            
            // Campo: Fecha de lanzamiento
            OutlinedTextField(
                value = uiState.releaseDate,
                onValueChange = viewModel::updateReleaseDate,
                label = { Text(stringResource(R.string.release_date)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                enabled = !uiState.isLoading,
                supportingText = {
                    Text(stringResource(R.string.date_format_hint))
                }
            )
            
            // Campo: Género (Dropdown)
            GenreDropdown(
                selectedGenre = uiState.genre,
                onGenreSelected = viewModel::updateGenre,
                enabled = !uiState.isLoading,
                isExpanded = uiState.isGenreDropdownExpanded,
                onExpandedChange = { expanded ->
                    viewModel.setGenreDropdownExpanded(expanded)
                }
            )
            
            // Campo: Discográfica (Dropdown)
            RecordLabelDropdown(
                selectedRecordLabel = uiState.recordLabel,
                onRecordLabelSelected = viewModel::updateRecordLabel,
                enabled = !uiState.isLoading,
                isExpanded = uiState.isRecordLabelDropdownExpanded,
                onExpandedChange = { expanded ->
                    viewModel.setRecordLabelDropdownExpanded(expanded)
                }
            )
            
            // Campo: Descripción
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 4,
                enabled = !uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón de crear
            Button(
                onClick = {
                    viewModel.createAlbum(onSuccess = onAlbumCreated)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.isValid()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (uiState.isLoading) {
                        stringResource(R.string.creating)
                    } else {
                        stringResource(R.string.create_album_button)
                    }
                )
            }
        }
    }
}

/**
 * Dropdown menu para seleccionar el género
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdown(
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    enabled: Boolean,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val genreOptions = CreateAlbumUiState.genreOptions
    
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { onExpandedChange(!isExpanded) },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.genre)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            genreOptions.forEach { genreOption ->
                DropdownMenuItem(
                    text = { Text(genreOption) },
                    onClick = {
                        onGenreSelected(genreOption)
                        onExpandedChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

/**
 * Dropdown menu para seleccionar la discográfica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordLabelDropdown(
    selectedRecordLabel: String,
    onRecordLabelSelected: (String) -> Unit,
    enabled: Boolean,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val recordLabelOptions = CreateAlbumUiState.recordLabelOptions
    
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { onExpandedChange(!isExpanded) },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRecordLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.record_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            recordLabelOptions.forEach { recordLabelOption ->
                DropdownMenuItem(
                    text = { Text(recordLabelOption) },
                    onClick = {
                        onRecordLabelSelected(recordLabelOption)
                        onExpandedChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
