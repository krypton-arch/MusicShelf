package com.example.musicshelf.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicshelf.data.local.dao.MoodTagDao
import com.example.musicshelf.data.local.dao.PlaylistDao
import com.example.musicshelf.data.local.dao.TrackDao
import com.example.musicshelf.data.local.entity.MoodTagEntity
import com.example.musicshelf.data.local.entity.PlaylistEntity
import com.example.musicshelf.data.local.entity.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlaylistEntity::class,
        TrackEntity::class,
        MoodTagEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicShelfDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun moodTagDao(): MoodTagDao

    companion object {
        const val DATABASE_NAME = "musicshelf_db"

        /**
         * Callback to seed default mood tags on database creation.
         */
        fun seedCallback(): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    "INSERT OR REPLACE INTO mood_tags (tag, color, emoji) VALUES " +
                            "('chill', '#64B5F6', '❄\uFE0F'), " +
                            "('hype', '#FF7043', '\uD83D\uDD25'), " +
                            "('focus', '#81C784', '\uD83C\uDFAF'), " +
                            "('sad', '#7986CB', '\uD83D\uDE22'), " +
                            "('party', '#FFD54F', '\uD83C\uDF89')"
                )
            }
        }
    }
}
