package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsportsColorScheme = darkColorScheme(
    primary = NeonPurpleBright,
    onPrimary = TextDark,
    primaryContainer = NeonPurple,
    onPrimaryContainer = TextWhite,
    secondary = FieryOrange,
    onSecondary = TextDark,
    secondaryContainer = EmberAmber,
    onSecondaryContainer = TextDark,
    tertiary = ElectricBlue,
    onTertiary = TextDark,
    background = EsportsBackground,
    onBackground = TextWhite,
    surface = EsportsSurface,
    onSurface = TextWhite,
    surfaceVariant = EsportsSurfaceVariant,
    onSurfaceVariant = TextMuted,
    error = ErrorRed,
    onError = TextWhite,
    outline = EsportsBorder,
    outlineVariant = NeonPurple.copy(alpha = 0.3f)
)

@Composable
fun AksharTournamentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Akshar Tournament uses an authentic esports dark neon scheme across all configurations
    MaterialTheme(
        colorScheme = EsportsColorScheme,
        typography = Typography,
        content = content
    )
}
