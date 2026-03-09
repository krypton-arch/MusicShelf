package com.example.musicshelf.ui.addtrack

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.entity.TrackEntity
import com.example.musicshelf.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddTrackUiState(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val durationMinutes: String = "",
    val durationSeconds: String = "",
    val isLoading: Boolean = false,
    val titleError: String? = null,
    val artistError: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddTrackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val trackRepository: TrackRepository
) : ViewModel() {

    val playlistId: String = savedStateHandle.get<String>("playlistId") ?: ""

    private val _uiState = MutableStateFlow(AddTrackUiState())
    val uiState: StateFlow<AddTrackUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun updateArtist(artist: String) {
        _uiState.update { it.copy(artist = artist, artistError = null) }
    }

    fun updateAlbum(album: String) {
        _uiState.update { it.copy(album = album) }
    }

    fun updateDurationMinutes(minutes: String) {
        if (minutes.all { it.isDigit() } && minutes.length <= 3) {
            _uiState.update { it.copy(durationMinutes = minutes) }
        }
    }

    fun updateDurationSeconds(seconds: String) {
        if (seconds.all { it.isDigit() } && seconds.length <= 2) {
            val value = seconds.toIntOrNull() ?: 0
            if (value <= 59) {
                _uiState.update { it.copy(durationSeconds = seconds) }
            }
        }
    }

    fun saveTrack() {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            hasError = true
        }
        if (state.artist.isBlank()) {
            _uiState.update { it.copy(artistError = "Artist is required") }
            hasError = true
        }
        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val minutes = state.durationMinutes.toIntOrNull() ?: 0
            val seconds = state.durationSeconds.toIntOrNull() ?: 0
            val durationMs = ((minutes * 60L) + seconds) * 1000L

            val maxPosition = trackRepository.getMaxPosition(playlistId)
            val track = TrackEntity(
                id = UUID.randomUUID().toString(),
                playlistId = playlistId,
                title = state.title.trim(),
                artist = state.artist.trim(),
                album = state.album.trim(),
                durationMs = durationMs,
                position = maxPosition + 1,
                addedAt = System.currentTimeMillis()
            )
            trackRepository.addTrack(track)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
