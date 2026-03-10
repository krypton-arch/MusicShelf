package com.example.musicshelf.core.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object CreatePlaylist : Routes("create_playlist")
    data object PlaylistDetail : Routes("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_detail/$playlistId"
    }
    data object AddTrack : Routes("add_track/{playlistId}") {
        fun createRoute(playlistId: String) = "add_track/$playlistId"
    }
    data object Settings : Routes("settings")
    data object SpotifyImport : Routes("spotify_import")
}
