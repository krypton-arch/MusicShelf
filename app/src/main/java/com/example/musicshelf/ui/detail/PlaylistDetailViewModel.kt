package com.example.musicshelf.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.datastore.SortOrder
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.data.local.entity.TrackEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import com.example.musicshelf.domain.repository.TrackRepository
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailUiState(
    val playlist: PlaylistEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val recentlyDeletedTrack: TrackEntity? = null
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val playlistId: String = savedStateHandle.get<String>("playlistId") ?: ""

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private val _activeSortOrder = MutableStateFlow<SortOrder?>(null)
    val activeSortOrder: StateFlow<SortOrder?> = _activeSortOrder.asStateFlow()

    val tracks: StateFlow<List<TrackEntity>> = combine(
        trackRepository.getTracksForPlaylist(playlistId),
        _activeSortOrder,
        userPrefsRepository.userPreferencesFlow
    ) { tracksList, activeSort, prefs ->
        val sortToApply = activeSort ?: prefs.defaultSortOrder
        when (sortToApply) {
            SortOrder.SORT_ORDER_BPM -> tracksList.sortedByDescending { it.bpm ?: 0 }
            SortOrder.SORT_ORDER_DURATION -> tracksList.sortedByDescending { it.durationMs }
            SortOrder.SORT_ORDER_DATE_ADDED -> tracksList.sortedByDescending { it.addedAt }
            SortOrder.SORT_ORDER_POSITION, SortOrder.UNRECOGNIZED -> tracksList.sortedBy { it.position }
            null -> tracksList.sortedBy { it.position }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            try {
                val playlist = playlistRepository.getPlaylistById(playlistId)
                _uiState.update {
                    it.copy(
                        playlist = playlist,
                        isLoading = false,
                        error = if (playlist == null) "Playlist not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun deleteTrack(track: TrackEntity) {
        viewModelScope.launch {
            trackRepository.deleteTrack(track)
            _uiState.update { it.copy(recentlyDeletedTrack = track) }
        }
    }

    fun undoDeleteTrack() {
        val track = _uiState.value.recentlyDeletedTrack ?: return
        viewModelScope.launch {
            trackRepository.addTrack(track)
            _uiState.update { it.copy(recentlyDeletedTrack = null) }
        }
    }

    fun clearRecentlyDeleted() {
        _uiState.update { it.copy(recentlyDeletedTrack = null) }
    }

    fun reorderTracks(reorderedTracks: List<TrackEntity>) {
        viewModelScope.launch {
            val updatedTracks = reorderedTracks.mapIndexed { index, track ->
                track.copy(position = index)
            }
            trackRepository.updateTrackPositions(updatedTracks)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _uiState.value.playlist?.let {
                playlistRepository.deletePlaylist(it)
            }
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _activeSortOrder.value = sortOrder
    }
}
