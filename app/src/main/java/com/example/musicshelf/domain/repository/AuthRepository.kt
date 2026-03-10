package com.example.musicshelf.domain.repository

import kotlinx.coroutines.flow.StateFlow

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean
)

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>
    
    suspend fun signInAnonymously(): Result<AuthUser>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signOut()
}
