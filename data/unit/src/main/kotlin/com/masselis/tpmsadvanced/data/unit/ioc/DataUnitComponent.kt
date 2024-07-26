package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.common.koinApplicationComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module


public interface DataUnitComponent {

    public fun unitPreferences(): UnitPreferences

    public companion object :
        DataUnitComponent,
        KoinComponent by koinApplicationComponent({
            modules(
                InternalModule,
                CoreCommonComponent.module,
            )
        }) {
        override fun unitPreferences(): UnitPreferences = get()
        public val module: Module = InternalModule
    }
}
