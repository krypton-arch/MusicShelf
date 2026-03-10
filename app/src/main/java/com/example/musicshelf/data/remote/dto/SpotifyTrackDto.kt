package com.example.musicshelf.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpotifyPlaylistTracksResponse(
    @SerializedName("items") val items: List<SpotifyTrackItemDto>,
    @SerializedName("next") val next: String?
)

data class SpotifyTrackItemDto(
    @SerializedName("track") val track: SpotifyTrackDto?
)

data class SpotifyTrackDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("artists") val artists: List<SpotifyArtistDto>?,
    @SerializedName("album") val album: SpotifyAlbumDto?,
    @SerializedName("duration_ms") val durationMs: Long?,
    @SerializedName("uri") val uri: String?
)

data class SpotifyArtistDto(
    @SerializedName("name") val name: String?
)

data class SpotifyAlbumDto(
    @SerializedName("name") val name: String?,
    @SerializedName("images") val images: List<SpotifyImageDto>?
)
