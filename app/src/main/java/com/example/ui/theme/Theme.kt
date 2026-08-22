package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BentoGreenAccent,
    onPrimary = Color.White,
    primaryContainer = BentoHeaderBg,
    onPrimaryContainer = BentoOnBackground,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = BentoMemoriesBg,
    onSecondaryContainer = BentoMemoriesText,
    tertiary = SageTertiary,
    onTertiary = Color.White,
    tertiaryContainer = BentoMyDayBg,
    onTertiaryContainer = BentoMyDayText,
    background = BentoBackground,
    onBackground = BentoOnBackground,
    surface = Color.White,
    onSurface = BentoOnBackground,
    surfaceVariant = BentoHeaderBg.copy(alpha = 0.5f),
    onSurfaceVariant = BentoOnBackground.copy(alpha = 0.8f),
    outline = BentoHeaderBorder,
    outlineVariant = BentoHeaderBorder.copy(alpha = 0.5f),
    error = BentoHelpRed,
    onError = Color.White,
    errorContainer = AlertRedContainer,
    onErrorContainer = BentoHelpRedDark
)

private val HighContrastColorScheme = lightColorScheme(
    primary = HighContrastPrimary,
    onPrimary = Color.White,
    primaryContainer = HighContrastYellow,
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF000000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color.Black,
    tertiary = HighContrastPrimary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color.Black,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = Color.White,
    onSurface = HighContrastText,
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

val BentoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) {
        HighContrastColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = BentoShapes,
        content = content
    )
}
