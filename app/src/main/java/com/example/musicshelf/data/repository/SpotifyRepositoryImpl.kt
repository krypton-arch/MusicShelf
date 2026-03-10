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
            val allItems = mutableListOf<com.example.musicshelf.data.remote.dto.SpotifyTrackItemDto>()
            var currentResponse = apiService.getPlaylistTracks(spotifyPlaylistId)
            allItems.addAll(currentResponse.items)
            
            while (currentResponse.next != null) {
                currentResponse = apiService.getPlaylistTracksUrl(currentResponse.next!!)
                allItems.addAll(currentResponse.items)
            }
            
            val entities = allItems.mapIndexedNotNull { index, itemDto ->
                val track = itemDto.track ?: return@mapIndexedNotNull null
                
                // Provide sensible defaults for missing data
                val trackName = track.name ?: "Unknown Track"
                val artistName = track.artists?.joinToString(", ") { it.name ?: "" } ?: "Unknown Artist"
                val albumName = track.album?.name ?: "Unknown Album"
                
                TrackEntity(
                    id = UUID.randomUUID().toString(),
                    playlistId = localPlaylistId,
                    title = trackName,
                    artist = artistName,
                    album = albumName,
                    durationMs = track.durationMs ?: 0L,
                    coverUri = track.album?.images?.firstOrNull()?.url,
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
