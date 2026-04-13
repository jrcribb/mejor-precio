package com.example.mejorprecio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColorScheme(
    primary = DarkAccent,
    background = DarkBg,
    surface = DarkCard,
    onPrimary = Color.White,
    onBackground = DarkInk,
    onSurface = DarkInk,
    error = DarkErr,
    outline = DarkLine
)

private val LightColorPalette = lightColorScheme(
    primary = LightAccent,
    background = LightBg,
    surface = LightCard,
    onPrimary = Color.White,
    onBackground = LightInk,
    onSurface = LightInk,
    error = LightErr,
    outline = LightLine
)

@Composable
fun MejorPrecioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    val typography = Typography(
        bodyLarge = TextStyle(fontSize = (16 * fontScale).sp),
        titleLarge = TextStyle(fontSize = (22 * fontScale).sp),
        labelMedium = TextStyle(fontSize = (12 * fontScale).sp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}
