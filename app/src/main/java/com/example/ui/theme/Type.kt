package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Bundled Noto Sans family: Latin + Gujarati + Devanagari at matched weights.
// Guarantees equal optical weight for trilingual labels and prevents matra
// clipping on devices without system Indic fonts. Requests above 700 resolve
// to the bundled bold cut.
val MannsaathiFontFamily = FontFamily(
    Font(R.font.mannsaathi_body, FontWeight.Medium),
    Font(R.font.mannsaathi_body, FontWeight.SemiBold),
    Font(R.font.mannsaathi_body, FontWeight.Bold),
    Font(R.font.mannsaathi_body, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with
val Typography =
  Typography(
    defaultFontFamily = MannsaathiFontFamily,
    bodyLarge =
      TextStyle(
        fontFamily = MannsaathiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
  )
