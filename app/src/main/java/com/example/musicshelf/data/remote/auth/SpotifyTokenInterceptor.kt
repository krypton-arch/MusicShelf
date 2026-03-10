package com.example.musicshelf.data.remote.auth

import android.content.SharedPreferences
import com.example.musicshelf.data.remote.api.SpotifyApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import dagger.Lazy

class SpotifyTokenInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val spotifyAuthManagerProvider: Lazy<SpotifyAuthManager>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Don't intercept the token exchange endpoint itself
        if (originalRequest.url.encodedPath.contains("api/token")) {
            return chain.proceed(originalRequest)
        }

        var accessToken = sharedPreferences.getString("spotify_access_token", null)

        if (accessToken != null) {
            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
            
            val response = chain.proceed(requestBuilder.build())
            
            // Handle HTTP 401 Unauthorized (Token expired)
            if (response.code == 401) {
                response.close()
                synchronized(this) {
                    val currentToken = sharedPreferences.getString("spotify_access_token", null)
                    if (currentToken == accessToken) {
                        // Token hasn't been refreshed by another thread, so refresh it now
                        accessToken = runBlocking {
                            spotifyAuthManagerProvider.get().refreshAccessToken()
                        }
                    } else {
                        // Token was already refreshed by another thread
                        accessToken = currentToken
                    }

                    if (accessToken != null) {
                        val newRequestBuilder = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $accessToken")
                        return chain.proceed(newRequestBuilder.build())
                    }
                }
            }
            return response
        }

        return chain.proceed(originalRequest)
    }
}
