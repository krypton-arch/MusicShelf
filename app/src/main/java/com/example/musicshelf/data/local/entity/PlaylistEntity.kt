package com.example.musicshelf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,          // UUID
    val name: String,
    val description: String,
    val coverUri: String?,
    val moodTag: String,                 // "chill", "hype", "focus", "sad", "party"
    val isCollaborative: Boolean = false,
    val spotifyId: String? = null,       // null if local-only
    val createdAt: Long,
    val updatedAt: Long
)
