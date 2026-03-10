package com.example.musicshelf.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.musicshelf.domain.repository.AuthRepository
import com.example.musicshelf.domain.repository.AuthUser
import com.example.musicshelf.data.remote.auth.SpotifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isSpotifyConnected: StateFlow<Boolean> = spotifyAuthManager.isConnected

    fun signInAnonymously() {
        viewModelScope.launch {
            authRepository.signInAnonymously()
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun initiateSpotifyAuthFlow(context: Context) {
        spotifyAuthManager.initiateAuthFlow(context)
    }

    fun disconnectSpotify() {
        spotifyAuthManager.disconnect()
    }
}
