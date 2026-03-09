package com.example.musicshelf.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicshelf.data.local.MusicShelfDatabase
import com.example.musicshelf.data.local.dao.MoodTagDao
import com.example.musicshelf.data.local.dao.PlaylistDao
import com.example.musicshelf.data.local.dao.TrackDao
import com.example.musicshelf.data.local.datastore.UserPreferencesSerializer
import com.example.musicshelf.data.local.datastore.UserPrefs
import com.example.musicshelf.data.local.entity.MoodTagEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicShelfDatabase {
        return Room.databaseBuilder(
            context,
            MusicShelfDatabase::class.java,
            MusicShelfDatabase.DATABASE_NAME
        )
            .addCallback(MusicShelfDatabase.seedCallback())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePlaylistDao(database: MusicShelfDatabase): PlaylistDao =
        database.playlistDao()

    @Provides
    fun provideTrackDao(database: MusicShelfDatabase): TrackDao =
        database.trackDao()

    @Provides
    fun provideMoodTagDao(database: MusicShelfDatabase): MoodTagDao {
        return database.moodTagDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserPrefs> {
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = { context.dataStoreFile("user_prefs.pb") }
        )
    }
}

