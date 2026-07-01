package com.gominitta.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =============================================================================
// Design Tokens — Shapes
// =============================================================================
// Corner-radius scale confirmed against the Figma hi-fi screens (approx,
// view-only): chips/inputs ~8-12, cards ~12-16, sheets ~16. Token names follow
// Material 3 shape scale: ExtraSmall / Small / Medium / Large / ExtraLarge / Full.
// =============================================================================

val GominittaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // chips, text fields
    small      = RoundedCornerShape(8.dp),   // cards (small)
    medium     = RoundedCornerShape(12.dp),  // cards (default)
    large      = RoundedCornerShape(16.dp),  // sheets, dialogs
    extraLarge = RoundedCornerShape(28.dp),  // FAB, large cards
)
