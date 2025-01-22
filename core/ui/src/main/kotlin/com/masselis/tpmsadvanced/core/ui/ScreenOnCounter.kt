package com.masselis.tpmsadvanced.core.ui

import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import java.util.concurrent.atomic.AtomicInteger

public interface ScreenOnCounter {

    public fun increment()

    public fun decrement()

    public class Activity private constructor(
        private val activity: android.app.Activity
    ) : ScreenOnCounter {

        private val screenOnCounter = AtomicInteger(0)

        override fun increment() {
            if (screenOnCounter.getAndIncrement() == 0)
                activity.window.addFlags(FLAG_KEEP_SCREEN_ON)
        }

        override fun decrement() {
            if (screenOnCounter.decrementAndGet() == 0)
                activity.window?.clearFlags(FLAG_KEEP_SCREEN_ON)
        }

        public companion object {
            public operator fun android.app.Activity.invoke(): Activity = Activity(this)
        }
    }
}
