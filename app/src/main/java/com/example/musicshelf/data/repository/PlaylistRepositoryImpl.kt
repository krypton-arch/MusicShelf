package com.example.musicshelf.data.repository

import com.example.musicshelf.data.local.dao.PlaylistDao
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import com.example.musicshelf.domain.repository.TrackRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val trackRepository: TrackRepository,
    private val firestore: FirebaseFirestore
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> =
        playlistDao.getAllPlaylists()

    override fun getPlaylistsByMoodTag(moodTag: String): Flow<List<PlaylistEntity>> =
        playlistDao.getPlaylistsByMoodTag(moodTag)

    override suspend fun getPlaylistById(id: String): PlaylistEntity? =
        playlistDao.getPlaylistById(id)

    override suspend fun createPlaylist(playlist: PlaylistEntity) =
        playlistDao.insertPlaylist(playlist)

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
        if (playlist.isCollaborative) {
            syncToFirestore(playlist)
        }
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
        if (playlist.isCollaborative) {
            firestore.collection("playlists").document(playlist.id).delete().await()
        }
    }

    override suspend fun deletePlaylistById(id: String) {
        val playlist = getPlaylistById(id)
        playlistDao.deletePlaylistById(id)
        if (playlist?.isCollaborative == true) {
            firestore.collection("playlists").document(id).delete().await()
        }
    }

    private suspend fun syncToFirestore(playlist: PlaylistEntity) {
        try {
            // Sync metadata
            firestore.collection("playlists").document(playlist.id)
                .set(playlist)
                .await()

            // Sync tracks
            val tracks = trackRepository.getTracksForPlaylistSync(playlist.id)
            val tracksCollection = firestore.collection("playlists")
                .document(playlist.id)
                .collection("tracks")

            // For simplicity in Task 2, we just overwrite/set all tracks
            // In a real production app, we might use a more granular diffing strategy
            tracks.forEach { track ->
                tracksCollection.document(track.id).set(track).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // In production, we should handle this error or retry
        }
    }
}
