package com.masselis.tpmsadvanced.data.app.ioc

import com.masselis.tpmsadvanced.core.common.koinApplicationComponent
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module

public interface DataAppComponent {

    public fun appPreferences(): AppPreferences

    public companion object : DataAppComponent,
        KoinComponent by koinApplicationComponent({ modules(InternalModule) }) {
        override fun appPreferences(): AppPreferences = get()
        public val module: Module = InternalModule
    }
}
