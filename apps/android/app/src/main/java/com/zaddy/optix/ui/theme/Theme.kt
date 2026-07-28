package com.zaddy.optix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OptixColorScheme = darkColorScheme(
    primary = OptixOrange,
    onPrimary = OptixTextPrimary,
    primaryContainer = OptixOrangeDark,
    onPrimaryContainer = OptixTextPrimary,
    secondary = OptixOrangeLight,
    onSecondary = OptixDarkBackground,
    background = OptixDarkBackground,
    onBackground = OptixTextPrimary,
    surface = OptixSurface,
    onSurface = OptixTextPrimary,
    surfaceVariant = OptixCardBg,
    onSurfaceVariant = OptixTextSecondary,
    outline = OptixCardBorder,
    error = OptixErrorRed,
    onError = OptixTextPrimary
)

@Composable
fun OptixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OptixColorScheme,
        content = content
    )
}
