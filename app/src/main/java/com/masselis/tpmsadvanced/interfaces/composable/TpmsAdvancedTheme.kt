package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun TpmsAdvancedTheme(
    colorScheme: ColorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = AppTypography,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = typography,
        content = content
    )
}