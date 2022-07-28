package com.masselis.tpmsadvanced.core.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.common.CommonInitializer
import com.masselis.tpmsadvanced.core.ioc.CoreComponent
import com.masselis.tpmsadvanced.core.ioc.DaggerCoreComponent
import com.masselis.tpmsadvanced.unit.interfaces.UnitInitializer
import com.masselis.tpmsadvanced.unit.interfaces.unitComponent

private lateinit var privateComponent: CoreComponent
val coreComponent get() = privateComponent

class CoreInitializer : Initializer<CoreComponent> {
    override fun create(context: Context): CoreComponent = DaggerCoreComponent
        .factory()
        .build(unitComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CommonInitializer::class.java,
        UnitInitializer::class.java
    )
}