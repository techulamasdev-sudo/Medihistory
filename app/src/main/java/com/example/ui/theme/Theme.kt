package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TealDarkPrimary,
    onPrimary = TealDarkContainer,
    primaryContainer = TealDarkContainer,
    onPrimaryContainer = TealDarkOnContainer,
    secondary = MedicalCyan,
    background = MedicalBgDark,
    surface = MedicalSurfaceDark,
    surfaceVariant = MedicalSurfaceVariantDark,
    outline = MedicalOutlineDark,
    onBackground = MedicalTextPrimaryDark,
    onSurface = MedicalTextPrimaryDark,
    onSurfaceVariant = MedicalTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealOnContainer,
    secondary = MedicalCyan,
    secondaryContainer = MedicalCyanContainer,
    onSecondaryContainer = MedicalCyanOnContainer,
    background = MedicalBgLight,
    surface = MedicalSurfaceLight,
    surfaceVariant = MedicalSurfaceVariantLight,
    outline = MedicalOutlineLight,
    onBackground = MedicalTextPrimaryLight,
    onSurface = MedicalTextPrimaryLight,
    onSurfaceVariant = MedicalTextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Fixed branded teal medical theme looks best for demo consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
