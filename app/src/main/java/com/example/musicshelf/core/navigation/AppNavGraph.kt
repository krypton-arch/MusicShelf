package com.example.musicshelf.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicshelf.ui.addtrack.AddTrackScreen
import com.example.musicshelf.ui.create.CreatePlaylistScreen
import com.example.musicshelf.ui.detail.PlaylistDetailScreen
import com.example.musicshelf.ui.home.HomeScreen
import com.example.musicshelf.ui.settings.SettingsScreen

private const val NAV_ANIM_DURATION = 300

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
        enterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(NAV_ANIM_DURATION)
            )
        },
        exitTransition = {
            fadeOut(tween(NAV_ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(NAV_ANIM_DURATION)
            )
        },
        popEnterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(NAV_ANIM_DURATION)
            )
        },
        popExitTransition = {
            fadeOut(tween(NAV_ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(NAV_ANIM_DURATION)
            )
        }
    ) {
        composable(Routes.Home.route) {
            HomeScreen(
                onPlaylistClick = { id -> navController.navigate(Routes.PlaylistDetail.createRoute(id)) },
                onCreatePlaylistClick = { navController.navigate(Routes.CreatePlaylist.route) },
                onSettingsClick = { navController.navigate(Routes.Settings.route) }
            )
        }

        composable(Routes.CreatePlaylist.route) {
            CreatePlaylistScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlaylistCreated = { playlistId ->
                    navController.popBackStack()
                    navController.navigate(Routes.PlaylistDetail.createRoute(playlistId))
                }
            )
        }

        composable(
            route = Routes.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) {
            PlaylistDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddTrackClick = { playlistId ->
                    navController.navigate(Routes.AddTrack.createRoute(playlistId))
                }
            )
        }

        composable(
            route = Routes.AddTrack.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) {
            AddTrackScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
