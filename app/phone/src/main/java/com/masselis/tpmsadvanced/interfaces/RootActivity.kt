package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.masselis.tpmsadvanced.core.ui.LocalKeepScreenOnCounter
import com.masselis.tpmsadvanced.core.ui.ScreenOnCounter
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

internal class RootActivity : AppCompatActivity() {

    private val counter = ScreenOnCounter.Activity()

    private val hasConsumedIntent = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState
            ?.getBoolean(STATE_HAS_CONSUMED_INTENT, false)
            .let { it ?: false }
            .also { hasConsumedIntent.set(it) }

        val expectedVehicle = intent
            ?.data
            ?.pathSegments
            ?.first()
            ?.let { UUID.fromString(it) }
            ?.takeIf { hasConsumedIntent.compareAndSet(false, true) }

        @Suppress("CAST_NEVER_SUCCEEDS")
        (this as ComponentActivity).setContent {
            CompositionLocalProvider(LocalKeepScreenOnCounter provides counter) {
                TpmsAdvancedTheme {
                    Main(expectedVehicle)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_HAS_CONSUMED_INTENT, hasConsumedIntent.get())
    }

    companion object {
        private const val STATE_HAS_CONSUMED_INTENT = "STATE_HAS_CONSUMED_INTENT"
    }
}
