package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.common.koinApplicationComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.ksp.generated.module

public interface DataUnitComponent {

    public val unitPreferences: UnitPreferences

    public companion object : DataUnitComponent,
        KoinComponent by koinApplicationComponent({
            modules(
                LocalModule.module,
                CoreCommonComponent.module,
            )
        }) {
        override val unitPreferences: UnitPreferences by inject()
    }
}
