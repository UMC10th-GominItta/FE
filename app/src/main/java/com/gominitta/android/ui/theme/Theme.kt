package com.gominitta.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

// =============================================================================
// Theme — wires design tokens into MaterialTheme
// =============================================================================
// ColorScheme is wired from the Figma-extracted tokens in Color.kt (light
// theme; the design is light-only, dark scheme is a derived convenience).
// =============================================================================

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary          = PrimaryDefault,
    onPrimary        = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    tertiary         = TertiaryDefault,
    onTertiary       = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    secondary        = SecondaryDefault,
    onSecondary      = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    surface          = SurfaceDefault,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    background       = BackgroundDefault,
    onBackground     = OnBackground,
    error            = ErrorDefault,
    errorContainer   = ErrorContainer,
    onError          = OnError,
    onErrorContainer = OnErrorContainer,
    outline          = OutlineDefault,
    outlineVariant   = OutlineVariant,
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary          = PrimaryContainer,
    onPrimary        = OnPrimaryContainer,
    primaryContainer = PrimaryDefault,
    onPrimaryContainer = OnPrimary,
    tertiary         = TertiaryContainer,
    onTertiary       = OnTertiaryContainer,
    tertiaryContainer = TertiaryDefault,
    onTertiaryContainer = OnTertiary,
    secondary        = SecondaryContainer,
    onSecondary      = OnSecondaryContainer,
    secondaryContainer = SecondaryDefault,
    onSecondaryContainer = OnSecondary,
    surface          = OnBackground,
    onSurface        = BackgroundDefault,
    background       = OnBackground,
    onBackground     = BackgroundDefault,
    error            = ErrorContainer,
    onError          = OnErrorContainer,
    errorContainer   = ErrorDefault,
    onErrorContainer = OnError,
)

/**
 * App-wide theme entry-point. Compose this at the root of every screen tree.
 *
 * Features:
 *  - Material 3 colour scheme with placeholder brand tokens
 *  - Dynamic colour (Android 12+) when [dynamicColor] is true
 *  - Dark-mode support
 *  - Custom spacing scale accessible via [MaterialTheme.spacing]
 */
@Composable
fun GominittaTheme(
    darkTheme:    Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,          // set true to enable Material You
    content:      @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = GominittaTypography,
            shapes      = GominittaShapes,
            content     = content,
        )
    }
}

/** Convenience accessor: `MaterialTheme.spacing.md` */
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
