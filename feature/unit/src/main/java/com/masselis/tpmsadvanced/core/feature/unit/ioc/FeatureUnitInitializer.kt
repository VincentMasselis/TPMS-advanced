package com.masselis.tpmsadvanced.core.feature.unit.ioc

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitInitializer
import com.masselis.tpmsadvanced.data.unit.ioc.dataUnitComponent

private lateinit var privateComponent: FeatureUnitComponent
public val featureUnitComponent: FeatureUnitComponent get() = privateComponent

public class FeatureUnitInitializer : Initializer<FeatureUnitComponent> {
    override fun create(context: Context): FeatureUnitComponent = DaggerFeatureUnitComponent
        .factory()
        .build(dataUnitComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java,
        DataUnitInitializer::class.java
    )
}
