package com.example.musicshelf.ui.importspotify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import com.example.musicshelf.domain.repository.SpotifyRepository
import com.example.musicshelf.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpotifyImportUiState(
    val playlists: List<PlaylistEntity> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val importSuccess: Int? = null
)

@HiltViewModel
class SpotifyImportViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotifyImportUiState())
    val uiState: StateFlow<SpotifyImportUiState> = _uiState.asStateFlow()

    init {
        loadSpotifyPlaylists()
    }

    private fun loadSpotifyPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            spotifyRepository.fetchCurrentUserPlaylists()
                .onSuccess { playlists ->
                    _uiState.update { it.copy(playlists = playlists, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun toggleSelection(playlistId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedIds.contains(playlistId)) {
                state.selectedIds - playlistId
            } else {
                state.selectedIds + playlistId
            }
            state.copy(selectedIds = newSelection)
        }
    }

    fun checkAll(check: Boolean) {
        _uiState.update { state ->
            val newSelection = if (check) state.playlists.map { it.id }.toSet() else emptySet()
            state.copy(selectedIds = newSelection)
        }
    }

    fun importSelected() {
        val state = _uiState.value
        val selectedPlaylists = state.playlists.filter { state.selectedIds.contains(it.id) }
        
        if (selectedPlaylists.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            var successCount = 0
            
            selectedPlaylists.forEach { playlist ->
                // 1. Create local playlist
                // We generate a new local ID unless we want to keep the Spotify ID as primary (spec says UUID)
                // SpotifyRepositoryImpl already generates a random UUID for 'id' and puts the real spotifyId in 'spotifyId'
                playlistRepository.createPlaylist(playlist)
                
                // 2. Fetch and save tracks
                playlist.spotifyId?.let { spotifyId ->
                    spotifyRepository.fetchPlaylistTracks(spotifyId, playlist.id)
                        .onSuccess { tracks ->
                            trackRepository.addTracks(tracks)
                            successCount++
                        }
                }
            }
            
            _uiState.update { it.copy(isLoading = false, importSuccess = successCount) }
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(importSuccess = null) }
    }
}
