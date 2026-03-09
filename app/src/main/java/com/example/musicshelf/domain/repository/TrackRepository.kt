package com.example.musicshelf.domain.repository

import com.example.musicshelf.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>>
    fun getTrackCountForPlaylist(playlistId: String): Flow<Int>
    suspend fun addTrack(track: TrackEntity)
    suspend fun addTracks(tracks: List<TrackEntity>)
    suspend fun deleteTrack(track: TrackEntity)
    suspend fun deleteTrackById(trackId: String)
    suspend fun updateTrackPositions(tracks: List<TrackEntity>)
    suspend fun getMaxPosition(playlistId: String): Int
}
