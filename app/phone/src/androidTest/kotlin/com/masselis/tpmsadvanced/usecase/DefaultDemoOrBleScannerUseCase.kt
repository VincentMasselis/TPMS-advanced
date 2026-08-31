package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import kotlinx.coroutines.flow.MutableStateFlow

internal class DefaultDemoOrBleScannerUseCase(isDemo: Boolean) : DemoOrBleScannerUseCase {
    override val isDemo = MutableStateFlow(isDemo)
    override fun demo() = error("Should be called during test")
    override fun ble() = error("Should be called during test")
}