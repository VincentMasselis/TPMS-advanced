package com.masselis.tpmsadvanced.data.vehicle.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.demo.DemoLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.BluetoothLeScannerImpl
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn


@Suppress("unused")
@BindingContainer
@ContributesTo(AppScope::class)
public object DemoOrBleScannerBinding {

    @Provides
    @SingleIn(AppScope::class)
    private fun demoOrBleScannerUseCase(context: Context): DemoOrBleScannerUseCase =
        DemoOrBleScannerUseCase.Impl(context)

    @Provides
    @SingleIn(AppScope::class)
    private fun bluetoothLeScannerImpl(
        demoOrBleScannerUseCase: DemoOrBleScannerUseCase,
        context: Context
    ): BluetoothLeScanner =
        if (demoOrBleScannerUseCase.isDemo.value) DemoLeScanner()
        else BluetoothLeScannerImpl(context)
}
