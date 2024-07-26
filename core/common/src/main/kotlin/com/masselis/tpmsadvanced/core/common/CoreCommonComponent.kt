package com.masselis.tpmsadvanced.core.common

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.ksp.generated.module

private val privateModule: Module = InternalModule.module

public interface CoreCommonComponent {

    public val context: Context

    public companion object :
        CoreCommonComponent,
        KoinComponent by koinApplicationComponent({ modules(privateModule) }) {
        override val context: Context get() = get()
        public val module: Module = privateModule
    }
}
