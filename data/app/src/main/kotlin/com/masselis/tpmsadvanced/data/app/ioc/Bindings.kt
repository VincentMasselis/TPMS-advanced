package com.masselis.tpmsadvanced.data.app.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {
    @Provides
    @SingleIn(AppScope::class)
    private fun appPreferences(context: Context): AppPreferences = AppPreferences(context)
}
