package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.common.koinApplicationComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.ksp.generated.module

@Module
public interface DataUnitComponent {

    @Factory
    public fun unitPreferences(): UnitPreferences

    public companion object :
            () -> DataUnitComponent,
        DataUnitComponent,
        KoinComponent by koinApplicationComponent({
            modules(
                LocalModule.module,
                CoreCommonComponent.module,
            )
        }) {
        override fun invoke(): DataUnitComponent = this
        override fun unitPreferences(): UnitPreferences = get()
    }
}
