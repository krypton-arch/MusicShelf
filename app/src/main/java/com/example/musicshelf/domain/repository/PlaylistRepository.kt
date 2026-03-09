package com.example.musicshelf.domain.repository

import com.example.musicshelf.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    fun getPlaylistsByMoodTag(moodTag: String): Flow<List<PlaylistEntity>>
    suspend fun getPlaylistById(id: String): PlaylistEntity?
    suspend fun createPlaylist(playlist: PlaylistEntity)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun deletePlaylist(playlist: PlaylistEntity)
    suspend fun deletePlaylistById(id: String)
}
