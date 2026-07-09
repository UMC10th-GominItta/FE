package com.gominitta.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.gominitta.android.R

// =============================================================================
// Design Tokens — Typography
// =============================================================================
// Sourced from the "고민이따" Figma text styles (page "Design 컴포넌트, 스타일,
// 텍스트"), 2026-07-08. 11 named styles — names mirror Figma 1:1 so hand-off maps
// directly. Every style: Pretendard, lineHeight 140% (1.4em), letterSpacing -2%
// (-0.02em). Name suffix: sb = SemiBold(600), m = Medium(500), r = Regular(400).
//
// Layer 1 = the Pretendard family + the named Figma styles — use these directly
// in Composables, e.g. `Text(..., style = Body2_15r)`.
// Layer 2 = GominittaTypography maps them onto the Material 3 type scale so
// `MaterialTheme.typography.*` and default components stay on-brand.
// =============================================================================

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular,  FontWeight.Normal),
    Font(R.font.pretendard_medium,   FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold,     FontWeight.Bold),
)

// --- Named Figma text styles (source of truth) ---
// All share Pretendard / lineHeight 140% / letterSpacing -2%; only size & weight vary.
private fun pretendardStyle(sizeSp: Int, weight: FontWeight) = TextStyle(
    fontFamily = Pretendard,
    fontWeight = weight,
    fontSize = sizeSp.sp,
    lineHeight = 1.4.em,          // 140%
    letterSpacing = (-0.02).em,   // -2%
)

val Heading1_24sb = pretendardStyle(24, FontWeight.SemiBold)
val Title1_20sb   = pretendardStyle(20, FontWeight.SemiBold)
val Heading2_20m  = pretendardStyle(20, FontWeight.Medium)
val Title2_18sb   = pretendardStyle(18, FontWeight.SemiBold)
val Heading3_18m  = pretendardStyle(18, FontWeight.Medium)
val Body1_16m     = pretendardStyle(16, FontWeight.Medium)
val Body2_15r     = pretendardStyle(15, FontWeight.Normal)
val Heading4_15m  = pretendardStyle(15, FontWeight.Medium)
val Button1_15m   = pretendardStyle(15, FontWeight.Medium)
val Body3_14r     = pretendardStyle(14, FontWeight.Normal)
val Caption1_12r  = pretendardStyle(12, FontWeight.Normal)

// --- Material 3 type scale (maps the named Figma styles onto Material slots) ---
// Notable defaults: default Text() = bodyLarge = Body2_15r; Material Button text =
// labelLarge = Button1_15m; NavigationBar label = labelMedium = Caption1_12r.
val GominittaTypography = Typography(
    displayLarge   = Heading1_24sb,
    displayMedium  = Heading1_24sb,
    displaySmall   = Heading1_24sb,

    headlineLarge  = Heading1_24sb,
    headlineMedium = Title1_20sb,
    headlineSmall  = Title2_18sb,

    titleLarge     = Title1_20sb,
    titleMedium    = Body1_16m,
    titleSmall     = Heading4_15m,

    bodyLarge      = Body2_15r,
    bodyMedium     = Body3_14r,
    bodySmall      = Caption1_12r,

    labelLarge     = Button1_15m,
    labelMedium    = Caption1_12r,
    labelSmall     = Caption1_12r,
)
