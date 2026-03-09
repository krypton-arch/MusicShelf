package com.example.musicshelf.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicshelf.R
import com.example.musicshelf.data.local.datastore.SortOrder
import com.example.musicshelf.data.local.datastore.ThemePreference
import com.example.musicshelf.ui.components.defaultMoodTags
import com.example.musicshelf.ui.theme.MusicPrimary
import com.example.musicshelf.ui.theme.MusicSurface
import com.example.musicshelf.ui.theme.MusicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPrefs by viewModel.userPrefs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Theme Setting
            SettingsSection(title = stringResource(R.string.settings_theme_title)) {
                SettingsOption(
                    title = stringResource(R.string.theme_system),
                    isSelected = userPrefs.themePreference == ThemePreference.THEME_PREFERENCE_SYSTEM ||
                            userPrefs.themePreference == ThemePreference.THEME_PREFERENCE_UNSPECIFIED,
                    onClick = { viewModel.setThemePreference(ThemePreference.THEME_PREFERENCE_SYSTEM) }
                )
                SettingsOption(
                    title = stringResource(R.string.theme_dark),
                    isSelected = userPrefs.themePreference == ThemePreference.THEME_PREFERENCE_DARK,
                    onClick = { viewModel.setThemePreference(ThemePreference.THEME_PREFERENCE_DARK) }
                )
                SettingsOption(
                    title = stringResource(R.string.theme_light),
                    isSelected = userPrefs.themePreference == ThemePreference.THEME_PREFERENCE_LIGHT,
                    onClick = { viewModel.setThemePreference(ThemePreference.THEME_PREFERENCE_LIGHT) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Default Sort Setting
            SettingsSection(title = stringResource(R.string.settings_default_sort_title)) {
                SettingsOption(
                    title = stringResource(R.string.sort_position),
                    isSelected = userPrefs.defaultSortOrder == SortOrder.SORT_ORDER_POSITION,
                    onClick = { viewModel.setDefaultSortOrder(SortOrder.SORT_ORDER_POSITION) }
                )
                SettingsOption(
                    title = stringResource(R.string.sort_bpm),
                    isSelected = userPrefs.defaultSortOrder == SortOrder.SORT_ORDER_BPM,
                    onClick = { viewModel.setDefaultSortOrder(SortOrder.SORT_ORDER_BPM) }
                )
                SettingsOption(
                    title = stringResource(R.string.sort_duration),
                    isSelected = userPrefs.defaultSortOrder == SortOrder.SORT_ORDER_DURATION,
                    onClick = { viewModel.setDefaultSortOrder(SortOrder.SORT_ORDER_DURATION) }
                )
                SettingsOption(
                    title = stringResource(R.string.sort_date_added),
                    isSelected = userPrefs.defaultSortOrder == SortOrder.SORT_ORDER_DATE_ADDED,
                    onClick = { viewModel.setDefaultSortOrder(SortOrder.SORT_ORDER_DATE_ADDED) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Default Mood Setting
            SettingsSection(title = stringResource(R.string.settings_default_mood_title)) {
                SettingsOption(
                    title = stringResource(R.string.mood_all),
                    isSelected = userPrefs.defaultMoodFilter.isEmpty(),
                    onClick = { viewModel.setDefaultMoodFilter(null) }
                )
                defaultMoodTags.forEach { mood ->
                    SettingsOption(
                        title = "${mood.emoji} ${mood.tag.replaceFirstChar { it.uppercase() }}",
                        isSelected = userPrefs.defaultMoodFilter == mood.tag,
                        onClick = { viewModel.setDefaultMoodFilter(mood.tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MusicPrimary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MusicSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MusicPrimary
            )
        }
    }
}
