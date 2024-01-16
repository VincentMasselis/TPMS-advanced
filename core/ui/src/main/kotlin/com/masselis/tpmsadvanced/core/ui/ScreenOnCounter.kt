package com.masselis.tpmsadvanced.core.ui

import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import java.util.concurrent.atomic.AtomicInteger

public interface ScreenOnCounter {

    public fun increment()

    public fun decrement()

    context (android.app.Activity)
    public class Activity : ScreenOnCounter {

        private val screenOnCounter = AtomicInteger(0)

        override fun increment() {
            if (screenOnCounter.getAndIncrement() == 0)
                window.addFlags(FLAG_KEEP_SCREEN_ON)
        }

        override fun decrement() {
            if (screenOnCounter.decrementAndGet() == 0)
                window?.clearFlags(FLAG_KEEP_SCREEN_ON)
        }
    }
}
