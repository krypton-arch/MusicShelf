package com.example.musicshelf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_tags")
data class MoodTagEntity(
    @PrimaryKey val tag: String,
    val color: String,                   // hex color string
    val emoji: String
)
