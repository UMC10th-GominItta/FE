package com.gominitta.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =============================================================================
// Design Tokens — Shapes
// =============================================================================
// PLACEHOLDER corner-radius values. Replace with real Figma shape tokens
// during the design-system extraction sprint. Token names follow Material 3
// shape scale: ExtraSmall / Small / Medium / Large / ExtraLarge / Full.
// =============================================================================

val GominittaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // placeholder — chips, text fields
    small      = RoundedCornerShape(8.dp),   // placeholder — cards (small)
    medium     = RoundedCornerShape(12.dp),  // placeholder — cards (default)
    large      = RoundedCornerShape(16.dp),  // placeholder — sheets, dialogs
    extraLarge = RoundedCornerShape(28.dp),  // placeholder — FAB, large cards
)
