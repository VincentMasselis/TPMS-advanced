package com.masselis.tpmsadvanced.core.common

import android.content.Context
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.ksp.generated.module

@Module
public interface CoreCommonComponent : () -> CoreCommonComponent {

    @Factory
    public fun context(): Context

    public companion object :
        CoreCommonComponent,
        KoinComponent by koinApplicationComponent({
            modules(LocalModule.module)
        }) {
        override fun invoke(): CoreCommonComponent = this
        override fun context(): Context = get()
    }
}
