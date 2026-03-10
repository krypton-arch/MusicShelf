package com.example.musicshelf.data.repository

import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.data.local.entity.TrackEntity
import com.example.musicshelf.data.remote.api.AddTracksRequest
import com.example.musicshelf.data.remote.api.CreatePlaylistRequest
import com.example.musicshelf.data.remote.api.SpotifyApiService
import com.example.musicshelf.domain.repository.SpotifyRepository
import java.util.UUID
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val apiService: SpotifyApiService
) : SpotifyRepository {

    override suspend fun fetchCurrentUserPlaylists(): Result<List<PlaylistEntity>> {
        return try {
            val response = apiService.getCurrentUserPlaylists()
            val entities = response.items.map { dto ->
                PlaylistEntity(
                    id = UUID.randomUUID().toString(),
                    name = dto.name,
                    description = dto.description ?: "",
                    coverUri = dto.images?.firstOrNull()?.url,
                    moodTag = "chill", // Default tag, could be customized or prompted
                    isCollaborative = false,
                    spotifyId = dto.id,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
            Result.success(entities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchPlaylistTracks(
        spotifyPlaylistId: String,
        localPlaylistId: String
    ): Result<List<TrackEntity>> {
        return try {
            val response = apiService.getPlaylistTracks(spotifyPlaylistId)
            val entities = response.items.mapIndexed { index, itemDto ->
                val track = itemDto.track
                TrackEntity(
                    id = UUID.randomUUID().toString(),
                    playlistId = localPlaylistId,
                    title = track.name,
                    artist = track.artists.joinToString(", ") { it.name },
                    album = track.album.name,
                    durationMs = track.durationMs,
                    coverUri = track.album.images?.firstOrNull()?.url,
                    spotifyUri = track.uri,
                    position = index,
                    addedAt = System.currentTimeMillis()
                )
            }
            Result.success(entities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSpotifyPlaylist(
        name: String,
        description: String,
        moodTag: String
    ): Result<PlaylistEntity> {
        return try {
            val user = apiService.getCurrentUser()
            val response = apiService.createPlaylist(
                userId = user.id,
                request = CreatePlaylistRequest(
                    name = name,
                    description = description,
                    isPublic = false
                )
            )
            val entity = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = response.name,
                description = response.description ?: "",
                coverUri = response.images?.firstOrNull()?.url,
                moodTag = moodTag,
                isCollaborative = false,
                spotifyId = response.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTracksToSpotifyPlaylist(
        spotifyPlaylistId: String,
        spotifyTrackUris: List<String>
    ): Result<Unit> {
        return try {
            if (spotifyTrackUris.isNotEmpty()) {
                // Spotify API allows up to 100 tracks per request, could chunk if larger
                val chunks = spotifyTrackUris.chunked(100)
                for (chunk in chunks) {
                    apiService.addTracksToPlaylist(
                        playlistId = spotifyPlaylistId,
                        request = AddTracksRequest(uris = chunk)
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
