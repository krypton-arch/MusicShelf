package com.example.musicshelf.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicshelf.ui.theme.MusicPrimary
import com.example.musicshelf.ui.theme.MusicTextSecondary

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    illustrationType: EmptyStateType = EmptyStateType.PLAYLIST
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (illustrationType) {
            EmptyStateType.PLAYLIST -> PlaylistEmptyIllustration()
            EmptyStateType.TRACK -> TrackEmptyIllustration()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MusicTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

enum class EmptyStateType {
    PLAYLIST, TRACK
}

@Composable
private fun PlaylistEmptyIllustration() {
    val primary = MusicPrimary
    val secondary = MusicTextSecondary

    Canvas(modifier = Modifier.size(120.dp)) {
        val w = size.width
        val h = size.height

        // Vinyl record circle
        drawCircle(
            color = primary.copy(alpha = 0.15f),
            radius = w * 0.4f,
            center = Offset(w * 0.5f, h * 0.45f)
        )

        drawCircle(
            color = primary.copy(alpha = 0.4f),
            radius = w * 0.4f,
            center = Offset(w * 0.5f, h * 0.45f),
            style = Stroke(width = 2.dp.toPx())
        )

        drawCircle(
            color = primary,
            radius = w * 0.12f,
            center = Offset(w * 0.5f, h * 0.45f)
        )

        drawCircle(
            color = Color(0xFF0A0A0F),
            radius = w * 0.04f,
            center = Offset(w * 0.5f, h * 0.45f)
        )

        // Music note
        val noteX = w * 0.72f
        val noteY = h * 0.2f
        drawLine(
            color = secondary,
            start = Offset(noteX, noteY),
            end = Offset(noteX, noteY + h * 0.25f),
            strokeWidth = 2.dp.toPx()
        )
        drawCircle(
            color = secondary,
            radius = w * 0.05f,
            center = Offset(noteX - w * 0.03f, noteY + h * 0.25f)
        )

        // Plus sign
        val plusX = w * 0.5f
        val plusY = h * 0.85f
        val plusSize = w * 0.08f
        drawLine(
            color = primary,
            start = Offset(plusX - plusSize, plusY),
            end = Offset(plusX + plusSize, plusY),
            strokeWidth = 2.5.dp.toPx()
        )
        drawLine(
            color = primary,
            start = Offset(plusX, plusY - plusSize),
            end = Offset(plusX, plusY + plusSize),
            strokeWidth = 2.5.dp.toPx()
        )
    }
}

@Composable
private fun TrackEmptyIllustration() {
    val primary = MusicPrimary
    val secondary = MusicTextSecondary

    Canvas(modifier = Modifier.size(120.dp)) {
        val w = size.width
        val h = size.height

        // List lines
        for (i in 0..2) {
            val y = h * 0.25f + i * h * 0.2f

            // Circle dot
            drawCircle(
                color = if (i == 0) primary else secondary.copy(alpha = 0.3f),
                radius = w * 0.03f,
                center = Offset(w * 0.15f, y)
            )

            // Line
            drawRoundRect(
                color = if (i == 0) primary.copy(alpha = 0.3f) else secondary.copy(alpha = 0.15f),
                topLeft = Offset(w * 0.25f, y - h * 0.015f),
                size = Size(w * (0.55f - i * 0.1f), h * 0.03f),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // Music note
        val noteX = w * 0.75f
        val noteY = h * 0.15f
        drawLine(
            color = primary.copy(alpha = 0.5f),
            start = Offset(noteX, noteY),
            end = Offset(noteX, noteY + h * 0.2f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawCircle(
            color = primary.copy(alpha = 0.5f),
            radius = w * 0.04f,
            center = Offset(noteX - w * 0.02f, noteY + h * 0.2f)
        )

        // Plus
        val plusY = h * 0.85f
        val plusSize = w * 0.06f
        drawLine(
            color = primary,
            start = Offset(w * 0.5f - plusSize, plusY),
            end = Offset(w * 0.5f + plusSize, plusY),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = primary,
            start = Offset(w * 0.5f, plusY - plusSize),
            end = Offset(w * 0.5f, plusY + plusSize),
            strokeWidth = 2.dp.toPx()
        )
    }
}
