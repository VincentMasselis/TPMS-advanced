package com.masselis.tpmsadvanced.data.record.ioc

import android.content.Context
import androidx.startup.Initializer

private lateinit var privateComponent: DataRecordComponent
public val dataRecordComponent: DataRecordComponent get() = privateComponent

public class DataRecordInitializer : Initializer<DataRecordComponent> {
    override fun create(context: Context): DataRecordComponent = DaggerDataRecordComponent
        .factory()
        .build()
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
