package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Native system font family supporting Latin, Gujarati, and Devanagari (Hindi)
// with native Unicode shaping, no matra clipping, and zero font loading latency.
val MannsaathiFontFamily = FontFamily.Default

// Standard Material 3 typography styles with system font fallback
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MannsaathiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
