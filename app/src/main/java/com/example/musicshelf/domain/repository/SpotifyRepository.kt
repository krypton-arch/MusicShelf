package com.example.musicshelf.domain.repository

import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.data.local.entity.TrackEntity

interface SpotifyRepository {
    suspend fun fetchCurrentUserPlaylists(): Result<List<PlaylistEntity>>
    suspend fun fetchPlaylistTracks(spotifyPlaylistId: String, localPlaylistId: String): Result<List<TrackEntity>>
    suspend fun createSpotifyPlaylist(name: String, description: String, moodTag: String): Result<PlaylistEntity>
    suspend fun addTracksToSpotifyPlaylist(spotifyPlaylistId: String, spotifyTrackUris: List<String>): Result<Unit>
}
