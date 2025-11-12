package com.mp3converter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

// Professional Dark Color Scheme - Inspired by modern music apps
private val DarkColorScheme = darkColorScheme(
    // Primary: Deep Purple/Indigo - Main brand color
    primary = Color(0xFF9C7AFF),
    onPrimary = Color(0xFF1A0033),
    primaryContainer = Color(0xFF5E35B1),
    onPrimaryContainer = Color(0xFFE8DDFF),

    // Secondary: Vibrant Coral/Pink - Accent color
    secondary = Color(0xFFFF6B9D),
    onSecondary = Color(0xFF3D001E),
    secondaryContainer = Color(0xFFC2185B),
    onSecondaryContainer = Color(0xFFFFD9E4),

    // Tertiary: Teal accent
    tertiary = Color(0xFF4DD0E1),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF00838F),
    onTertiaryContainer = Color(0xFFB2EBF2),

    // Error colors
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF370000),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFDAD6),

    // Background: Deep dark grey (not pure black)
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFE8E8E8),

    // Surface: Slightly lighter than background for cards
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCACACA),

    // Outline
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF333333),
)

// Professional Light Color Scheme - Clean and modern
private val LightColorScheme = lightColorScheme(
    // Primary: Rich Purple - Main brand color
    primary = Color(0xFF6200EA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1D7FF),
    onPrimaryContainer = Color(0xFF1D004D),

    // Secondary: Vibrant Pink - Accent color
    secondary = Color(0xFFD81B60),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E4),
    onSecondaryContainer = Color(0xFF3E001E),

    // Tertiary: Deep Teal
    tertiary = Color(0xFF00838F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB2EBF2),
    onTertiaryContainer = Color(0xFF00363D),

    // Error colors
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF370000),

    // Background: Clean white
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    // Surface: White for cards with subtle elevation
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF464646),

    // Outline
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
)

@Composable
fun Mp3ConverterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled for consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use surface color for status bar for better integration
            window.statusBarColor = colorScheme.surface.toArgb()
            // Make icons dark in light mode, light in dark mode
            val insetsController = window.insetsController
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.setSystemBarsAppearance(
                    if (darkTheme) 0 else android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
