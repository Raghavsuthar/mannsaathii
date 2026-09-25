package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Native system font family supporting Latin, Gujarati, and Devanagari (Hindi)
// with native Unicode shaping, no matra clipping, and zero font loading latency.
val MannsaathiFontFamily = FontFamily.Default

// Set of Material typography styles
private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = MannsaathiFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = MannsaathiFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = MannsaathiFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = MannsaathiFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = MannsaathiFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = MannsaathiFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = MannsaathiFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = MannsaathiFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = MannsaathiFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = MannsaathiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = MannsaathiFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = MannsaathiFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = MannsaathiFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = MannsaathiFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = MannsaathiFontFamily)
)
