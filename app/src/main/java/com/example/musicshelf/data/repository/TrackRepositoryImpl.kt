package com.example.musicshelf.data.repository

import com.example.musicshelf.data.local.dao.TrackDao
import com.example.musicshelf.data.local.entity.TrackEntity
import com.example.musicshelf.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao
) : TrackRepository {

    override fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>> =
        trackDao.getTracksForPlaylist(playlistId)

    override fun getTrackCountForPlaylist(playlistId: String): Flow<Int> =
        trackDao.getTrackCountForPlaylist(playlistId)

    override suspend fun addTrack(track: TrackEntity) =
        trackDao.insertTrack(track)

    override suspend fun addTracks(tracks: List<TrackEntity>) =
        trackDao.insertTracks(tracks)

    override suspend fun deleteTrack(track: TrackEntity) =
        trackDao.deleteTrack(track)

    override suspend fun deleteTrackById(trackId: String) =
        trackDao.deleteTrackById(trackId)

    override suspend fun updateTrackPositions(tracks: List<TrackEntity>) {
        tracks.forEachIndexed { index, track ->
            trackDao.updateTrackPosition(track.id, index)
        }
    }

    override suspend fun getMaxPosition(playlistId: String): Int =
        trackDao.getMaxPosition(playlistId) ?: -1
}
