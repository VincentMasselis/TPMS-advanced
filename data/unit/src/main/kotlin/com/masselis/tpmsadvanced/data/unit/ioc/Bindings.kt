package com.masselis.tpmsadvanced.data.unit.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {
    @Provides
    @SingleIn(AppScope::class)
    private fun unitPreferences(context: Context): UnitPreferences = UnitPreferences(context)
}
