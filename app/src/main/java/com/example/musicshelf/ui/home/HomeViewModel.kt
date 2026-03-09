package com.example.musicshelf.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import com.example.musicshelf.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistWithTrackCount(
    val playlist: PlaylistEntity,
    val trackCount: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedMood = MutableStateFlow<String?>(null)
    val selectedMood: StateFlow<String?> = _selectedMood.asStateFlow()

    init {
        viewModelScope.launch {
            userPrefsRepository.userPreferencesFlow.collect { userPreferences ->
                userPreferences.defaultMoodFilter.let { defaultMood ->
                    if (defaultMood.isNotEmpty()) {
                        _selectedMood.value = defaultMood
                    }
                }
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val playlists: StateFlow<List<PlaylistWithTrackCount>> = _selectedMood
        .flatMapLatest { mood ->
            if (mood == null) {
                playlistRepository.getAllPlaylists()
            } else {
                playlistRepository.getPlaylistsByMoodTag(mood)
            }
        }
        .map { playlistList ->
            playlistList.map { playlist ->
                PlaylistWithTrackCount(
                    playlist = playlist,
                    trackCount = 0 // Will be populated below
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setMoodFilter(mood: String?) {
        _selectedMood.value = mood
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlist)
        }
    }
}
