package com.example.musicshelf.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpotifyPlaylistsResponse(
    @SerializedName("items") val items: List<SpotifyPlaylistDto>
)

data class SpotifyPlaylistDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("images") val images: List<SpotifyImageDto>?,
    @SerializedName("public") val isPublic: Boolean?
)

data class SpotifyImageDto(
    @SerializedName("url") val url: String
)

data class SpotifyUserDto(
    @SerializedName("id") val id: String
)
