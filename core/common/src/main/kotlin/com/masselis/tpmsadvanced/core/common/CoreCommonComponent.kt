package com.masselis.tpmsadvanced.core.common

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.ksp.generated.module

public interface CoreCommonComponent {

    public val context: Context

    public companion object :
        CoreCommonComponent,
        KoinComponent by koinApplicationComponent(appDeclaration = {
            modules(FirebaseModule.module)
        }) {
        override val context: Context by inject()
    }
}
