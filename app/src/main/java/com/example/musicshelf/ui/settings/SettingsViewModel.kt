package com.example.musicshelf.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicshelf.data.local.datastore.SortOrder
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.data.local.datastore.UserPrefs
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPrefs: StateFlow<UserPrefs> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPrefs.getDefaultInstance()
        )

    fun setThemePreference(themePreference: ThemePreference) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemePreference(themePreference)
        }
    }

    fun setDefaultMoodFilter(moodTag: String?) {
        viewModelScope.launch {
            userPreferencesRepository.updateDefaultMoodFilter(moodTag)
        }
    }

    fun setDefaultSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.updateDefaultSortOrder(sortOrder)
        }
    }
}
