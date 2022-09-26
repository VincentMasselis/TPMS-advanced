package com.masselis.tpmsadvanced.data.unit.ioc

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer
import com.masselis.tpmsadvanced.core.common.coreCommonComponent

private lateinit var privateComponent: DataUnitComponent
public val dataUnitComponent: DataUnitComponent get() = privateComponent

public class DataUnitInitializer : Initializer<DataUnitComponent> {
    override fun create(context: Context): DataUnitComponent = DaggerDataUnitComponent
        .factory()
        .build(coreCommonComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java
    )
}
