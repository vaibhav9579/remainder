package com.remainder.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Roboto is the platform default sans-serif on Android, matching the spec's
// initial typography direction without pulling in a font dependency.
private val RemainderFontFamily = FontFamily.Default

// Screen title: 24sp / Bold
private val ScreenTitle = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
)

// Section title: 18sp / SemiBold
private val SectionTitle = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
)

// Action title: 16sp / Medium
private val ActionTitle = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
)

// Body: 14sp / Regular
private val Body = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

// Secondary: 13sp / Regular
private val Secondary = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
)

// Small label: 12sp / Medium
private val SmallLabel = TextStyle(
    fontFamily = RemainderFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
)

val RemainderTypography = Typography(
    headlineSmall = ScreenTitle,
    titleLarge = SectionTitle,
    titleMedium = ActionTitle,
    bodyLarge = Body,
    bodyMedium = Secondary,
    labelMedium = SmallLabel,
)
