package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.masselis.tpmsadvanced.core.ui.LocalKeepScreenOnCounter
import com.masselis.tpmsadvanced.core.ui.ScreenOnCounter
import com.masselis.tpmsadvanced.core.ui.saveable
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme
import java.util.UUID

internal class RootActivity : AppCompatActivity() {

    private val counter = ScreenOnCounter.Activity()

    private var hasConsumedIntent by saveable { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expectedVehicle = intent
            ?.data
            ?.pathSegments
            ?.first()
            ?.let { UUID.fromString(it) }
            ?.takeIf { hasConsumedIntent.not() }
            ?.also { hasConsumedIntent = true }

        setContent {
            CompositionLocalProvider(LocalKeepScreenOnCounter provides counter) {
                TpmsAdvancedTheme {
                    Main(expectedVehicle)
                }
            }
        }
    }
}
