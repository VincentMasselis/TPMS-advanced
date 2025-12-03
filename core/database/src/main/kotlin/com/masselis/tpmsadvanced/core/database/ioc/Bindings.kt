package com.masselis.tpmsadvanced.core.database.ioc

import android.content.Context
import com.masselis.tpmsadvanced.core.database.SQLiteOpenHelperUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    @SingleIn(AppScope::class)
    private fun sQLiteOpenHelperUseCase(context: Context): SQLiteOpenHelperUseCase =
        SQLiteOpenHelperUseCase(context)
}
