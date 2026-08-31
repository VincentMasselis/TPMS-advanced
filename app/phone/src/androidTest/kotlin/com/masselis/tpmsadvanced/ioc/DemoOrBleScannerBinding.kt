package com.masselis.tpmsadvanced.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import com.masselis.tpmsadvanced.usecase.DefaultDemoOrBleScannerUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.masselis.tpmsadvanced.data.vehicle.ioc.DemoOrBleScannerBinding as OriginalDemoOrBleScannerBinding

@ContributesTo(AppScope::class, replaces = [OriginalDemoOrBleScannerBinding::class])
public interface DemoOrBleScannerBinding {
    @Provides
    private fun demoOrBleScannerUseCase(context: Context): DemoOrBleScannerUseCase =
        DefaultDemoOrBleScannerUseCase(true)
}
