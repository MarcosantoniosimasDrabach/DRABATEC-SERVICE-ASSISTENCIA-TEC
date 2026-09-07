package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = DrabatecBlueAccent,
    onSecondary = Color.White,
    tertiary = DrabatecTealAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFF1F0F5),
    onSurface = Color(0xFFF1F0F5),
    outline = Color(0xFF5A5266),
    error = DrabatecError
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DrabatecPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = DrabatecPurpleContainer,
    onPrimaryContainer = DrabatecDeepViolet,
    secondary = DrabatecBlueAccent,
    onSecondary = Color.White,
    tertiary = DrabatecTealAccent,
    background = DrabatecBackground,
    surface = DrabatecSurface,
    surfaceVariant = DrabatecSurfaceVariant,
    onBackground = DrabatecTextPrimary,
    onSurface = DrabatecTextPrimary,
    outline = DrabatecOutline,
    error = DrabatecError
  )

@Composable
fun DrabatecServiceTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  DrabatecServiceTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
