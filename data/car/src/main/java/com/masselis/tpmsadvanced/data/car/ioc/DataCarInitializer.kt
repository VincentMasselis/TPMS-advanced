package com.masselis.tpmsadvanced.data.car.ioc

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer

private lateinit var privateInitializer: DataCarComponent
public val dataCarComponent: DataCarComponent get() = privateInitializer

internal class DataCarInitializer : Initializer<DataCarComponent> {

    override fun create(context: Context): DataCarComponent = DaggerDataCarComponent
        .factory()
        .build()
        .also { privateInitializer = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java
    )
}
