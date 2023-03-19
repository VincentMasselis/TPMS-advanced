package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.masselis.tpmsadvanced.core.ui.LocalKeepScreenOnCounter
import com.masselis.tpmsadvanced.core.ui.ScreenOnCounter
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme
import java.util.concurrent.atomic.AtomicInteger

internal class RootActivity : AppCompatActivity(), ScreenOnCounter {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalKeepScreenOnCounter provides this) {
                TpmsAdvancedTheme {
                    Main()
                }
            }
        }
    }

    private val screenOnCounter = AtomicInteger(0)

    override fun increment() {
        if (screenOnCounter.getAndIncrement() == 0)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun decrement() {
        if (screenOnCounter.decrementAndGet() == 0)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
