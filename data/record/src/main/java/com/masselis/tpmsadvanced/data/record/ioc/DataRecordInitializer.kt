package com.masselis.tpmsadvanced.data.record.ioc

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer
import com.masselis.tpmsadvanced.core.common.coreCommonComponent

private lateinit var privateComponent: DataRecordComponent
public val dataRecordComponent: DataRecordComponent get() = privateComponent

public class DataRecordInitializer : Initializer<DataRecordComponent> {
    override fun create(context: Context): DataRecordComponent = DaggerDataRecordComponent
        .factory()
        .build(coreCommonComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java
    )
}
