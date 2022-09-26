package com.masselis.tpmsadvanced.core.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

@SuppressLint("StaticFieldLeak")
private lateinit var privateContext: Context
public val appContext: Context get() = privateContext

private lateinit var privateComponent: CoreCommonComponent
public val coreCommonComponent: CoreCommonComponent get() = privateComponent

public class CoreCommonInitializer : Initializer<CoreCommonComponent> {
    override fun create(context: Context): CoreCommonComponent {
        privateContext = context
        return DaggerCoreCommonComponent
            .factory()
            .build(context)
            .also { privateComponent = it }
            // Initialize firebase and crashlytics
            .also { it.firebaseApp;it.crashlytics }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
