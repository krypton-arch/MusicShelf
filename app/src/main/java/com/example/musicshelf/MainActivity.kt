package com.example.musicshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import com.example.musicshelf.core.navigation.AppNavGraph
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import com.example.musicshelf.ui.theme.MusicShelfTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPrefs by userPreferencesRepository.userPreferencesFlow.collectAsStateWithLifecycle(
                initialValue = com.example.musicshelf.data.local.datastore.UserPrefs.getDefaultInstance()
            )

            val useDarkTheme = when (userPrefs.themePreference) {
                ThemePreference.THEME_PREFERENCE_LIGHT -> false
                ThemePreference.THEME_PREFERENCE_DARK -> true
                else -> isSystemInDarkTheme() // THEME_PREFERENCE_SYSTEM or UNSPECIFIED
            }

            MusicShelfTheme(darkTheme = useDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}