package com.example.musicshelf.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playlistId")]
)
data class TrackEntity(
    @PrimaryKey val id: String,
    val playlistId: String,              // FK → PlaylistEntity
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val bpm: Int? = null,
    val coverUri: String? = null,
    val spotifyUri: String? = null,
    val localUri: String? = null,        // for offline files
    val position: Int,
    val addedAt: Long
)
