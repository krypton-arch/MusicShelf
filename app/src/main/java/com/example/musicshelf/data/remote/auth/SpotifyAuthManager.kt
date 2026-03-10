package com.example.musicshelf.data.remote.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import com.example.musicshelf.BuildConfig
import com.example.musicshelf.data.remote.api.SpotifyApiService
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyAuthManager @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val sharedPreferences: android.content.SharedPreferences
) {

    companion object {
        private const val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID
        private const val REDIRECT_URI = BuildConfig.SPOTIFY_REDIRECT_URI
        private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    }

    private var currentCodeVerifier: String? = null

    private val _isConnected = kotlinx.coroutines.flow.MutableStateFlow(
        sharedPreferences.getString("spotify_access_token", null) != null
    )
    val isConnected: kotlinx.coroutines.flow.StateFlow<Boolean> = kotlinx.coroutines.flow.asStateFlow(_isConnected)

    fun initiateAuthFlow(context: Context) {
        val codeVerifier = generateCodeVerifier()
        currentCodeVerifier = codeVerifier
        val codeChallenge = generateCodeChallenge(codeVerifier)

        val uri = Uri.parse(AUTH_URL).buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("scope", "playlist-read-private playlist-read-collaborative playlist-modify-private playlist-modify-public")
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, uri)
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(32)
        secureRandom.nextBytes(codeVerifier)
        return Base64.encodeToString(codeVerifier, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun handleAuthCallback(intent: Intent): String? {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "musicshelf" && data.host == "callback") {
            return data.getQueryParameter("code")
        }
        return null
    }

    suspend fun exchangeCodeForToken(code: String) {
        val verifier = currentCodeVerifier ?: return
        try {
            val response = spotifyApiService.getAccessToken(
                clientId = CLIENT_ID,
                code = code,
                redirectUri = REDIRECT_URI,
                codeVerifier = verifier
            )
            saveTokens(response.accessToken, response.refreshToken)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun refreshAccessToken(): String? {
        val refreshToken = sharedPreferences.getString("spotify_refresh_token", null) ?: return null
        return try {
            val response = spotifyApiService.refreshAccessToken(
                clientId = CLIENT_ID,
                refreshToken = refreshToken
            )
            val newRefreshToken = response.refreshToken ?: refreshToken
            saveTokens(response.accessToken, newRefreshToken)
            response.accessToken
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String?) {
        sharedPreferences.edit()
            .putString("spotify_access_token", accessToken)
            .apply {
                if (refreshToken != null) {
                    putString("spotify_refresh_token", refreshToken)
                }
            }
            .apply()
        _isConnected.value = true
    }

    fun disconnect() {
        sharedPreferences.edit()
            .remove("spotify_access_token")
            .remove("spotify_refresh_token")
            .apply()
        _isConnected.value = false
    }

    fun getCodeVerifier(): String? = currentCodeVerifier
}
