package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import com.masselis.tpmsadvanced.interfaces.composable.AppTypography
import com.masselis.tpmsadvanced.interfaces.composable.DarkColors
import com.masselis.tpmsadvanced.interfaces.composable.LightColors
import com.masselis.tpmsadvanced.interfaces.composable.Main

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
                typography = AppTypography,
            ) {
                Main()
            }
        }
    }
}