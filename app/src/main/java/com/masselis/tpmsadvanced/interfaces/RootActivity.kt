package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TpmsAdvancedTheme {
                Main()
            }
        }
    }
}