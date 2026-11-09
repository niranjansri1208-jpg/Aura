package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AuraColorScheme = darkColorScheme(
    primary = AuraCyan,
    onPrimary = AuraBackground,
    primaryContainer = AuraSurfaceElevated,
    onPrimaryContainer = AuraCyan,
    secondary = AuraElectricBlue,
    onSecondary = AuraBackground,
    secondaryContainer = AuraSurface,
    onSecondaryContainer = AuraElectricBlue,
    tertiary = AuraCyberViolet,
    onTertiary = Color.White,
    background = AuraBackground,
    onBackground = AuraTextPrimary,
    surface = AuraSurface,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraSurfaceElevated,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraBorder,
    outlineVariant = AuraBorderGlowing,
    error = AuraCrimson,
    onError = Color.White
)

@Composable
fun AuraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards-compatible name for existing references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuraTheme(content = content)
}
