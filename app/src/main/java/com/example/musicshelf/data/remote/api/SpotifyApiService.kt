package com.example.musicshelf.data.remote.api

import com.example.musicshelf.data.remote.dto.SpotifyPlaylistDto
import com.example.musicshelf.data.remote.dto.SpotifyPlaylistsResponse
import com.example.musicshelf.data.remote.dto.SpotifyPlaylistTracksResponse
import com.example.musicshelf.data.remote.dto.SpotifyUserDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("v1/me")
    suspend fun getCurrentUser(): SpotifyUserDto

    @GET("v1/me/playlists")
    suspend fun getCurrentUserPlaylists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): SpotifyPlaylistsResponse

    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("playlist_id") playlistId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): SpotifyPlaylistTracksResponse

    @POST("v1/users/{user_id}/playlists")
    suspend fun createPlaylist(
        @Path("user_id") userId: String,
        @Body request: CreatePlaylistRequest
    ): SpotifyPlaylistDto

    @POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Path("playlist_id") playlistId: String,
        @Body request: AddTracksRequest
    ): AddTracksResponse

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): SpotifyTokenResponse

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String
    ): SpotifyTokenResponse
}

data class SpotifyTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("scope") val scope: String?
)

data class CreatePlaylistRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("public") val isPublic: Boolean
)

data class AddTracksRequest(
    @SerializedName("uris") val uris: List<String>
)

data class AddTracksResponse(
    @SerializedName("snapshot_id") val snapshotId: String
)
