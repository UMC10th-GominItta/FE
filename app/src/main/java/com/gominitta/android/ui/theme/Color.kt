package com.gominitta.android.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// Design Tokens — Color
// =============================================================================
// Values extracted from the "고민이따" Figma file (Color style guide on the
// "디자인 1차 작업" page), 2026-07-01. Ramp hex values are precise (read from the
// guide's swatch labels). The neutral Text_Gray mid-steps and the semantic
// role mapping are best-effort (view-only extraction, no Dev Mode).
//
// Layer 1 = raw palette ramps (100 lightest → 900 darkest, 500 = base).
// Layer 2 = semantic role aliases consumed by Theme.kt. Reassign these to
// remap the Material scheme without touching Theme.kt.
// =============================================================================

// --- Palette: Primary_IB (Ivory/Beige — warm neutral, app background) ---
val IB100 = Color(0xFFFEFEFC)
val IB200 = Color(0xFFFDFCFA)
val IB300 = Color(0xFFFBF9F6)
val IB400 = Color(0xFFF7F5F1)
val IB500 = Color(0xFFF3F0EB)
val IB600 = Color(0xFFE1DAD0)
val IB700 = Color(0xFFAFA497)
val IB800 = Color(0xFF766B60)
val IB900 = Color(0xFF5D5044)

// --- Palette: Primary_BR (Brown/Tan) ---
val BR100 = Color(0xFFFDFBF4)
val BR200 = Color(0xFFFBF6EA)
val BR300 = Color(0xFFF5ECDC)
val BR400 = Color(0xFFECDFCE)
val BR500 = Color(0xFFE0CEBA)
val BR600 = Color(0xFFB6A391)
val BR700 = Color(0xFF907D6E)
val BR800 = Color(0xFF6C5B50)
val BR900 = Color(0xFF52433C)

// --- Palette: Secondary_YW (Soft Yellow) ---
val YW100 = Color(0xFFFFFEF6)
val YW200 = Color(0xFFFFFCED)
val YW300 = Color(0xFFFFFBE4)
val YW400 = Color(0xFFFFF9DE)
val YW500 = Color(0xFFFFF7D3)
val YW600 = Color(0xFFF7F0D4)
val YW700 = Color(0xFFEBE4CC)
val YW800 = Color(0xFFBBB39B)
val YW900 = Color(0xFF746F63)

// --- Palette: Secondary_OG (Peach/Orange) ---
val OG100 = Color(0xFFFEFBF1)
val OG200 = Color(0xFFFEF7E4)
val OG300 = Color(0xFFFDF1D6)
val OG400 = Color(0xFFFBEACB)
val OG500 = Color(0xFFF9E0BA)
val OG600 = Color(0xFFDEC7AB)
val OG700 = Color(0xFFAD9C8A)
val OG800 = Color(0xFF7C6D5F)
val OG900 = Color(0xFF5B5149)

// --- Palette: Text_Gray (neutral; anchors precise, mid-steps approximate) ---
val Gray50  = Color(0xFFFAFAFA)
val Gray500 = Color(0xFF737373)
val Gray900 = Color(0xFF171717)

// --- Semantic: Brand primary (dark brown for filled actions) ---
val PrimaryDefault      = BR800                 // #6C5B50
val PrimaryContainer    = BR300                 // #F5ECDC
val OnPrimary           = Color(0xFFFFFFFF)
val OnPrimaryContainer  = BR900                 // #52433C

// --- Semantic: Secondary (peach accent) ---
val SecondaryDefault     = OG500                // #F9E0BA
val SecondaryContainer   = OG300                // #FDF1D6
val OnSecondary          = BR900                // #52433C
val OnSecondaryContainer = OG900                // #5B5149

// --- Semantic: Tertiary (muted yellow accent) ---
val TertiaryDefault      = YW800                // #BBB39B
val TertiaryContainer    = YW300                // #FFFBE4
val OnTertiary           = BR900                // #52433C
val OnTertiaryContainer  = YW900                // #746F63

// --- Semantic: Surface & background ---
val SurfaceDefault    = IB100                   // #FEFEFC near-white card
val SurfaceVariant    = IB400                   // #F7F5F1
val OnSurface         = Gray900                 // #171717
val OnSurfaceVariant  = Gray500                 // #737373
val BackgroundDefault = IB500                   // #F3F0EB app canvas
val OnBackground      = Gray900                 // #171717

// --- Semantic: Status (no dedicated swatch in Figma — Material defaults) ---
val ErrorDefault      = Color(0xFFB3261E)
val ErrorContainer    = Color(0xFFF9DEDC)
val OnError           = Color(0xFFFFFFFF)
val OnErrorContainer  = Color(0xFF410E0B)

// --- Semantic: Outline ---
val OutlineDefault    = IB700                   // #AFA497
val OutlineVariant    = IB600                   // #E1DAD0
