package com.example.musicshelf.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicshelf.R
import com.example.musicshelf.ui.components.EmptyStateType
import com.example.musicshelf.ui.components.EmptyStateView
import com.example.musicshelf.ui.components.MoodChip
import com.example.musicshelf.ui.components.MoodTagInfo
import com.example.musicshelf.ui.components.defaultMoodTags
import com.example.musicshelf.ui.theme.MusicPrimary
import com.example.musicshelf.ui.theme.MusicTextSecondary
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onPlaylistClick: (String) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMood.collectAsStateWithLifecycle()

    // Staggered entrance animation state for filter chips
    var chipsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150) // slight delay after screen appears
        chipsVisible = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePlaylistClick,
                containerColor = MusicPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_create_playlist)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Mood Filter Chips with staggered spring animation
            val allChips = listOf(MoodTagInfo("all", "🎵", MusicPrimary)) + defaultMoodTags

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(allChips) { index, mood ->
                    AnimatedVisibility(
                        visible = chipsVisible,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = 300f
                            ),
                            initialOffsetX = { fullWidth -> fullWidth * (index + 1) }
                        )
                    ) {
                        MoodChip(
                            moodTag = mood,
                            isSelected = if (mood.tag == "all") selectedMood == null else selectedMood == mood.tag,
                            onClick = {
                                viewModel.setMoodFilter(if (mood.tag == "all") null else mood.tag)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playlists Grid
            Box(modifier = Modifier.weight(1f)) {
                if (playlists.isEmpty() && selectedMood == null) {
                    EmptyStateView(
                        title = stringResource(R.string.empty_playlists_title),
                        subtitle = stringResource(R.string.empty_playlists_subtitle),
                        illustrationType = EmptyStateType.PLAYLIST
                    )
                } else {
                    AnimatedContent(
                        targetState = playlists,
                        transitionSpec = {
                            fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                        },
                        label = "PlaylistGridAnimation"
                    ) { animatedPlaylists ->
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 8.dp,
                                bottom = 100.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(animatedPlaylists, key = { it.playlist.id }) { playlistWithCount ->
                                PlaylistCard(
                                    playlistWithCount = playlistWithCount,
                                    onClick = { onPlaylistClick(playlistWithCount.playlist.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
