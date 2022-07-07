package com.masselis.tpmsadvanced.interfaces

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.masselis.tpmsadvanced.interfaces.composable.Main
import com.masselis.tpmsadvanced.interfaces.composable.TpmsAdvancedTheme
import java.util.concurrent.atomic.AtomicInteger

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TpmsAdvancedTheme {
                Main()
            }
        }
    }

    private val screenOnCounter = AtomicInteger(0)

    fun incrementScreenOnCounter() {
        if (screenOnCounter.getAndIncrement() == 0)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun decrementScreenOnCounter() {
        if(screenOnCounter.decrementAndGet() == 0)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}