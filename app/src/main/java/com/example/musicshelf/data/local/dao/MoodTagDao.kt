package com.example.musicshelf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicshelf.data.local.entity.MoodTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodTagDao {

    @Query("SELECT * FROM mood_tags ORDER BY tag ASC")
    fun getAllMoodTags(): Flow<List<MoodTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(moodTags: List<MoodTagEntity>)
}
