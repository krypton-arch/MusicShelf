package com.example.musicshelf.domain.repository

import com.example.musicshelf.data.local.datastore.SortOrder
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.data.local.datastore.UserPrefs
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPrefs>

    suspend fun updateThemePreference(themePreference: ThemePreference)
    suspend fun updateDefaultMoodFilter(moodTag: String?)
    suspend fun updateDefaultSortOrder(sortOrder: SortOrder)
    suspend fun updateOnboardingSeen(seen: Boolean)
}
