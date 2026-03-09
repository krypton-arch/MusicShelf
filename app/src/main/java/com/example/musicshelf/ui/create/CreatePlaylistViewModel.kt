package com.example.musicshelf.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreatePlaylistUiState(
    val name: String = "",
    val description: String = "",
    val selectedMoodTag: String = "chill",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val createdPlaylistId: String? = null
)

@HiltViewModel
class CreatePlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePlaylistUiState())
    val uiState: StateFlow<CreatePlaylistUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name, nameError = null)
        }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun selectMoodTag(tag: String) {
        _uiState.update { it.copy(selectedMoodTag = tag) }
    }

    fun createPlaylist() {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Playlist name is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val playlist = PlaylistEntity(
                id = id,
                name = currentState.name.trim(),
                description = currentState.description.trim(),
                coverUri = null,
                moodTag = currentState.selectedMoodTag,
                isCollaborative = false,
                spotifyId = null,
                createdAt = now,
                updatedAt = now
            )
            playlistRepository.createPlaylist(playlist)
            _uiState.update {
                it.copy(isLoading = false, createdPlaylistId = id)
            }
        }
    }
}
