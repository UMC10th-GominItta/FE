package com.gominitta.android.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// Design Tokens — Spacing
// =============================================================================
// Spacing scale on a 4dp base grid; matches the Figma 8px rhythm (screen/card
// padding 16, inter-card gap 12-16).
// Access via MaterialTheme.spacing.* inside composables (see Theme.kt).
// =============================================================================

@Immutable
data class Spacing(
    val none:   Dp = 0.dp,
    val xxs:    Dp = 2.dp,
    val xs:     Dp = 4.dp,
    val sm:     Dp = 8.dp,
    val md:     Dp = 16.dp,
    val lg:     Dp = 24.dp,
    val xl:     Dp = 32.dp,
    val xxl:    Dp = 48.dp,
    val xxxl:   Dp = 64.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
