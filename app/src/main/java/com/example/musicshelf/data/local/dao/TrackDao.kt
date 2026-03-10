package com.example.musicshelf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicshelf.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getTracksForPlaylistSync(playlistId: String): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM tracks WHERE playlistId = :playlistId")
    fun getTrackCountForPlaylist(playlistId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tracks WHERE playlistId = :playlistId")
    suspend fun getTrackCountSync(playlistId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("UPDATE tracks SET position = :position WHERE id = :trackId")
    suspend fun updateTrackPosition(trackId: String, position: Int)

    @Query("SELECT MAX(position) FROM tracks WHERE playlistId = :playlistId")
    suspend fun getMaxPosition(playlistId: String): Int?
}
