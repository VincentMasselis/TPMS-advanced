package com.masselis.tpmsadvanced.data.vehicle.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.ui.restartApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public interface DemoOrBleScannerUseCase {
    public val isDemo: StateFlow<Boolean>
    public fun demo()
    public fun ble()

    public class Impl internal constructor(
        private val context: Context,
    ) : DemoOrBleScannerUseCase {

        private val sp = context.getSharedPreferences(
            "com.masselis.tpmsadvanced.data.vehicle.interfaces.ScannerDatabase",
            Context.MODE_PRIVATE
        )

        override val isDemo: MutableStateFlow<Boolean> = MutableStateFlow(isDemo())

        /** ⚠️ Switching to `demo` triggers an application restart ! */
        override fun demo() {
            if (isDemo()) return
            sp.edit(commit = true) { putBoolean(IS_DEMO_KEY, true) }
            // A restart is mandatory to replace the implementation of BluetoothLeScanner returned
            // by the DI.
            context.restartApp()
        }

        /** ⚠️ Switching to `ble` triggers an application restart ! */
        override fun ble() {
            if (isDemo().not()) return
            sp.edit(commit = true) { putBoolean(IS_DEMO_KEY, false) }
            // A restart is mandatory to replace the implementation of BluetoothLeScanner returned
            // by the DI.
            context.restartApp()
        }

        private fun isDemo() = sp.getBoolean(IS_DEMO_KEY, false)

        internal companion object {
            private const val IS_DEMO_KEY = "is_demo"
        }
    }
}
