package com.masselis.tpmsadvanced.data.vehicle.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.DelicateCoroutinesApi


@OptIn(DelicateCoroutinesApi::class)
@Suppress("unused")
@ContributesTo(AppScope::class)
public interface DemoOrBleScannerBinding {
    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @SingleIn(AppScope::class)
    private fun demoOrBleScannerUseCase(context: Context): DemoOrBleScannerUseCase =
        DemoOrBleScannerUseCase.Impl(context)
}
