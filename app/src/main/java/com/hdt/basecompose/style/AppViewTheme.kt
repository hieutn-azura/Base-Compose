package com.hdt.basecompose.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = AppColors.Primary,
    onPrimary        = Color.White,
    primaryContainer = AppColors.PrimaryContainer,
    secondary        = AppColors.Secondary,
    onSecondary      = Color.White,
    background       = AppColors.Background,
    surface          = AppColors.Surface,
    onBackground     = AppColors.OnBackground,
    onSurface        = AppColors.OnSurface,
    error            = AppColors.Error,
)

private val DarkColorScheme = darkColorScheme(
    primary          = AppColors.PrimaryDark,
    onPrimary        = Color.Black,
    primaryContainer = AppColors.PrimaryContainerDark,
    secondary        = AppColors.SecondaryDark,
    onSecondary      = Color.Black,
    background       = AppColors.BackgroundDark,
    surface          = AppColors.SurfaceDark,
    onBackground     = AppColors.OnBackgroundDark,
    onSurface        = AppColors.OnSurfaceDark,
    error            = AppColors.Error,
)

@Composable
fun AppViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = AppTypography,
        content     = content,
    )
}
