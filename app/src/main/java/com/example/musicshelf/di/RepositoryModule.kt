package com.example.musicshelf.di

import com.example.musicshelf.data.repository.PlaylistRepositoryImpl
import com.example.musicshelf.data.repository.TrackRepositoryImpl
import com.example.musicshelf.data.repository.UserPreferencesRepositoryImpl
import com.example.musicshelf.domain.repository.PlaylistRepository
import com.example.musicshelf.domain.repository.TrackRepository
import com.example.musicshelf.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.musicshelf.domain.repository.SpotifyRepository
import com.example.musicshelf.data.repository.SpotifyRepositoryImpl
import com.example.musicshelf.domain.repository.AuthRepository
import com.example.musicshelf.data.repository.FirebaseAuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindTrackRepository(
        trackRepositoryImpl: TrackRepositoryImpl
    ): TrackRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindSpotifyRepository(
        spotifyRepositoryImpl: SpotifyRepositoryImpl
    ): SpotifyRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}
