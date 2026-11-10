package com.ptilou.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PtilouColorScheme = lightColorScheme(
    primary = PtilouOrange,
    onPrimary = PtilouSurface,
    secondary = PtilouBlue,
    tertiary = PtilouGreen,
    error = PtilouRed,
    background = PtilouBackground,
    onBackground = PtilouTextDark,
    surface = PtilouSurface,
    onSurface = PtilouTextDark
)

@Composable
fun PtilouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PtilouColorScheme,
        typography = PtilouTypography,
        content = content
    )
}
