package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.savedstate.serialization.saved
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.core.ui.LocalKeepScreenOnCounter
import com.masselis.tpmsadvanced.core.ui.ScreenOnCounter
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme
import com.masselis.tpmsadvanced.ioc.AppGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import java.util.UUID

public class RootActivity : AppCompatActivity() {

    private val counter = ScreenOnCounter.Activity()

    private var hasConsumedIntent by saved { false }

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
            CompositionLocalProvider(
                LocalKeepScreenOnCounter provides counter,
                LocalMetroViewModelFactory provides (appGraph as AppGraph).metroViewModelFactory,
            ) {
                TpmsAdvancedTheme {
                    Main(expectedVehicle)
                }
            }
        }
    }
}
