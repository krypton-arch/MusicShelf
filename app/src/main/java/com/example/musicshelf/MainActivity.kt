package com.example.musicshelf

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.musicshelf.core.navigation.AppNavGraph
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.data.remote.auth.SpotifyAuthManager
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import com.example.musicshelf.ui.theme.MusicShelfTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var spotifyAuthManager: SpotifyAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle intent if the app was killed and recreated from the deep link
        handleSpotifyIntent(intent)
        
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSpotifyIntent(intent)
    }

    private fun handleSpotifyIntent(intent: Intent?) {
        if (intent == null) return
        
        val code = spotifyAuthManager.handleAuthCallback(intent)
        if (code != null) {
            lifecycleScope.launch {
                val result = spotifyAuthManager.exchangeCodeForToken(code)
                if (result.isFailure) {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        "Spotify Auth Failed: ${result.exceptionOrNull()?.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                } else {
                    android.widget.Toast.makeText(this@MainActivity, "Spotify Connected!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        } else if (intent?.data?.scheme == "musicshelf" && intent.data?.getQueryParameter("error") != null) {
            android.widget.Toast.makeText(
                this, 
                "Spotify Auth Error: ${intent.data?.getQueryParameter("error")}", 
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }
}