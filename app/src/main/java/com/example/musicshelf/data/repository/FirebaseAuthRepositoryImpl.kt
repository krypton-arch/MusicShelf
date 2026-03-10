package com.example.musicshelf.data.repository

import com.example.musicshelf.domain.repository.AuthRepository
import com.example.musicshelf.domain.repository.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user?.toAuthUser()
        }
    }

    override suspend fun signInAnonymously(): Result<AuthUser> {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user?.toAuthUser() ?: throw Exception("Anonymous sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.toAuthUser() ?: throw Exception("Google sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        isAnonymous = isAnonymous
    )
}
