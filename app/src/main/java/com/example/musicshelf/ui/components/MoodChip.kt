package com.example.musicshelf.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicshelf.ui.theme.MoodChill
import com.example.musicshelf.ui.theme.MoodFocus
import com.example.musicshelf.ui.theme.MoodHype
import com.example.musicshelf.ui.theme.MoodParty
import com.example.musicshelf.ui.theme.MoodSad
import com.example.musicshelf.ui.theme.MusicPrimary
import com.example.musicshelf.ui.theme.MusicSurface
import com.example.musicshelf.ui.theme.MusicSurfaceVariant
import com.example.musicshelf.ui.theme.MusicTextSecondary

data class MoodTagInfo(
    val tag: String,
    val emoji: String,
    val color: Color
)

val defaultMoodTags = listOf(
    MoodTagInfo("chill", "❄\uFE0F", MoodChill),
    MoodTagInfo("hype", "\uD83D\uDD25", MoodHype),
    MoodTagInfo("focus", "\uD83C\uDFAF", MoodFocus),
    MoodTagInfo("sad", "\uD83D\uDE22", MoodSad),
    MoodTagInfo("party", "\uD83C\uDF89", MoodParty)
)

fun getMoodColor(tag: String): Color {
    return defaultMoodTags.find { it.tag == tag }?.color ?: MusicPrimary
}

fun getMoodEmoji(tag: String): String {
    return defaultMoodTags.find { it.tag == tag }?.emoji ?: "🎵"
}

@Composable
fun MoodChip(
    moodTag: MoodTagInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipColor = moodTag.color

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = "${moodTag.emoji} ${moodTag.tag.replaceFirstChar { it.uppercase() }}"
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MusicSurfaceVariant,
            labelColor = MusicTextSecondary,
            selectedContainerColor = chipColor.copy(alpha = 0.2f),
            selectedLabelColor = chipColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Color.Transparent,
            selectedBorderColor = chipColor.copy(alpha = 0.5f),
            enabled = true,
            selected = isSelected
        )
    )
}
