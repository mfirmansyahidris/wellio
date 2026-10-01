package com.wellio.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Forest = Color(0xFF326654)
val Ink = Color(0xFF243C32)
val Cream = Color(0xFFF8F8F2)
val Lime = Color(0xFFE7EFC7)
val Peach = Color(0xFFF9E3D1)
@Composable fun WellioTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Forest, onPrimary = Color.White,
        primaryContainer = Lime, onPrimaryContainer = Ink, background = Cream,
        surface = Cream, onSurface = Ink, onBackground = Ink,
        surfaceVariant = Color(0xFFEEF0E7), secondaryContainer = Peach), content = content)
}
