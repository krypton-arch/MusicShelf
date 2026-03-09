package com.example.musicshelf.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.musicshelf.data.local.datastore.SortOrder
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.data.local.datastore.UserPrefs
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<UserPrefs>
) : UserPreferencesRepository {

    private val TAG = "UserPreferencesRepo"

    override val userPreferencesFlow: Flow<UserPrefs> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading user preferences.", exception)
                emit(UserPrefs.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun updateThemePreference(themePreference: ThemePreference) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setThemePreference(themePreference).build()
        }
    }

    override suspend fun updateDefaultMoodFilter(moodTag: String?) {
        dataStore.updateData { preferences ->
            val builder = preferences.toBuilder()
            if (moodTag == null) {
                builder.clearDefaultMoodFilter()
            } else {
                builder.setDefaultMoodFilter(moodTag)
            }
            builder.build()
        }
    }

    override suspend fun updateDefaultSortOrder(sortOrder: SortOrder) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setDefaultSortOrder(sortOrder).build()
        }
    }

    override suspend fun updateOnboardingSeen(seen: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setOnboardingSeen(seen).build()
        }
    }
}
