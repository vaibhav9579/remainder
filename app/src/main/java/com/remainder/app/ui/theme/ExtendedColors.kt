package com.remainder.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colors outside Material 3's ColorScheme (success/warning),
 * needed for reminder priority and status indicators.
 */
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
)

val LightExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = Color(0xFFFFFFFF),
    warning = WarningColor,
    onWarning = Color(0xFF17181C),
)

val DarkExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = Color(0xFFFFFFFF),
    warning = WarningColor,
    onWarning = Color(0xFF0F1117),
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }
