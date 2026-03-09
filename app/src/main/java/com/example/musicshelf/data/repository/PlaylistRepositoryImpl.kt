package com.example.musicshelf.data.repository

import com.example.musicshelf.data.local.dao.PlaylistDao
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> =
        playlistDao.getAllPlaylists()

    override fun getPlaylistsByMoodTag(moodTag: String): Flow<List<PlaylistEntity>> =
        playlistDao.getPlaylistsByMoodTag(moodTag)

    override suspend fun getPlaylistById(id: String): PlaylistEntity? =
        playlistDao.getPlaylistById(id)

    override suspend fun createPlaylist(playlist: PlaylistEntity) =
        playlistDao.insertPlaylist(playlist)

    override suspend fun updatePlaylist(playlist: PlaylistEntity) =
        playlistDao.updatePlaylist(playlist)

    override suspend fun deletePlaylist(playlist: PlaylistEntity) =
        playlistDao.deletePlaylist(playlist)

    override suspend fun deletePlaylistById(id: String) =
        playlistDao.deletePlaylistById(id)
}
