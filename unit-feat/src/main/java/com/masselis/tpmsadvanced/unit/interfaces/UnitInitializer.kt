package com.masselis.tpmsadvanced.unit.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.common.CommonInitializer
import com.masselis.tpmsadvanced.unit.ioc.DaggerUnitComponent
import com.masselis.tpmsadvanced.unit.ioc.UnitComponent

private lateinit var privateComponent: UnitComponent
val unitComponent get() = privateComponent

class UnitInitializer : Initializer<UnitComponent> {
    override fun create(context: Context): UnitComponent = DaggerUnitComponent
        .factory()
        .build()
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CommonInitializer::class.java
    )
}