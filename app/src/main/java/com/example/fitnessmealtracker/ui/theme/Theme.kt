package com.example.fitnessmealtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF03A9F4),
    surface = Color(0xFF121212),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF5350)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF0288D1),
    surface = Color(0xFFF8F9FA),
    surfaceVariant = Color(0xFFEDF2F7),
    onSurface = Color(0xFF1A1A1A),
    error = Color(0xFFD32F2F)
)

@Composable
fun FitnessMealTrackerTheme(
    darkTheme: Boolean = true, // По подразбиране стилен тъмен режим за перфектен вид на S23 FE
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}