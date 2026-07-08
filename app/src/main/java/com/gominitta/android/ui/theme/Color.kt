package com.gominitta.android.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// Design Tokens — Color
// =============================================================================
// Sourced from the "고민이따" Figma **Color Styles** (page "Design 컴포넌트,
// 스타일, 텍스트"), cross-checked against the final hi-fi screens (2026-07-08).
// Layer-1 names mirror the Figma styles 1:1 (Primary / Gray / Accent·Cream /
// White) so design hand-off maps directly. Warm taupe + cream, light theme only.
//
// Layer 2 = semantic role aliases consumed by Theme.kt — remap these to
// restyle the Material scheme without touching Theme.kt.
// =============================================================================

// --- Figma Color Styles: Primary (warm taupe) ---
val Primary800 = Color(0xFF534B42)   // dark warm — primary/heading text, selected nav, on-accent
val Primary400 = Color(0xFFD0C1AB)   // taupe — unselected nav, outlines, illustration line
val Primary300 = Color(0xFFECDFCE)   // light warm tan (e.g. card border)
val Primary200 = Color(0xFFF3F0EB)   // pale — chips, input fills, nav bar

// --- Figma Color Styles: Gray (neutral) ---
val Gray800 = Color(0xFF404040)      // body text
val Gray600 = Color(0xFF737373)      // muted / secondary text
val Gray400 = Color(0xFFA6A6A6)      // placeholder text
val Gray200 = Color(0xFFD4D4D4)      // disabled, subtle border

// --- Figma Color Styles: White ---
val White800 = Color(0xFFFEFEFB)     // warm near-white — app canvas, cards

// --- Figma Color Styles: Accent / Cream (peach) ---
val AccentCream300 = Color(0xFFF9E0BA)   // primary CTA fill
val AccentCream200 = Color(0xFFFBEACB)   // soft peach-cream (also ElevatedCard shadow)
val AccentCream100 = Color(0xFFFDF1D6)   // slider track, soft accent

// --- Semantic: Primary = peach CTA ---
val PrimaryDefault      = AccentCream300   // #F9E0BA
val PrimaryContainer    = AccentCream200   // #FBEACB (kept; Primary300 redefined to #ECDFCE)
val OnPrimary           = Primary800       // #534B42
val OnPrimaryContainer  = Primary800       // #534B42

// --- Semantic: Secondary = taupe ---
val SecondaryDefault     = Primary400      // #D0C1AB
val SecondaryContainer   = Primary200      // #F3F0EB
val OnSecondary          = Primary800      // #534B42
val OnSecondaryContainer = Primary800      // #534B42

// --- Semantic: Tertiary = soft cream accent ---
val TertiaryDefault      = AccentCream100  // #FDF1D6
val TertiaryContainer    = AccentCream100  // #FDF1D6
val OnTertiary           = Primary800      // #534B42
val OnTertiaryContainer  = Primary800      // #534B42

// --- Semantic: Surface & background ---
val SurfaceDefault    = White800           // #FEFEFB near-white card
val SurfaceVariant    = Primary200         // #F3F0EB
val OnSurface         = Gray800            // #404040
val OnSurfaceVariant  = Gray600            // #737373
val BackgroundDefault = White800           // #FEFEFB app canvas
val OnBackground      = Gray800            // #404040

// --- Semantic: Status (no semantic error/success in the design — Material default) ---
val ErrorDefault      = Color(0xFFB3261E)
val ErrorContainer    = Color(0xFFF9DEDC)
val OnError           = Color(0xFFFFFFFF)
val OnErrorContainer  = Color(0xFF410E0B)

// --- Semantic: Outline ---
val OutlineDefault    = Primary400         // #D0C1AB
val OutlineVariant    = Gray200            // #D4D4D4
