package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.Component

@SingleInstance
@Component(
    dependencies = [CoreCommonComponent::class]
)
public interface DataUnitComponent {
    @Component.Factory
    public interface Factory {
        public fun build(coreCommonComponent: CoreCommonComponent): DataUnitComponent
    }

    public val unitPreferences: UnitPreferences

    public companion object : DataUnitComponent by DaggerDataUnitComponent
        .factory()
        .build(CoreCommonComponent)
}
