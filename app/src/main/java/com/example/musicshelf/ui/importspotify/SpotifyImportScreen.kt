package com.example.musicshelf.ui.importspotify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.musicshelf.R
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.ui.theme.MusicPrimary
import com.example.musicshelf.ui.theme.MusicSurfaceVariant
import com.example.musicshelf.ui.theme.MusicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyImportScreen(
    onNavigateBack: () -> Unit,
    onImportFinished: () -> Unit,
    viewModel: SpotifyImportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.importSuccess) {
        uiState.importSuccess?.let { count ->
            snackbarHostState.showSnackbar(
                message = "Imported $count playlists successfully!",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccess()
            onImportFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.spotify_import_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                actions = {
                    if (uiState.playlists.isNotEmpty()) {
                        TextButton(
                            onClick = { 
                                val allChecked = uiState.selectedIds.size == uiState.playlists.size
                                viewModel.checkAll(!allChecked)
                            }
                        ) {
                            Text(
                                text = if (uiState.selectedIds.size == uiState.playlists.size) 
                                    stringResource(R.string.uncheck_all) 
                                else stringResource(R.string.check_all),
                                color = MusicPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.selectedIds.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { viewModel.importSelected() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MusicPrimary)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.import_button, uiState.selectedIds.size))
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.playlists.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MusicPrimary
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                uiState.playlists.isEmpty() -> {
                    Text(
                        text = "No Spotify playlists found.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MusicTextSecondary
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.playlists) { playlist ->
                            SpotifyPlaylistImportItem(
                                playlist = playlist,
                                isSelected = uiState.selectedIds.contains(playlist.id),
                                onToggle = { viewModel.toggleSelection(playlist.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpotifyPlaylistImportItem(
    playlist: PlaylistEntity,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MusicSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverUri != null) {
                AsyncImage(
                    model = playlist.coverUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MusicPrimary.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (playlist.description.isNotBlank()) {
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MusicTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) MusicPrimary else MusicTextSecondary
            )
        }
    }
}
