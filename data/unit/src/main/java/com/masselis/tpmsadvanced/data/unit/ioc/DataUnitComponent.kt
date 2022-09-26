package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.Component

@SingleInstance
@Component(
    dependencies = [CoreCommonComponent::class]
)
public abstract class DataUnitComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(coreCommonComponent: CoreCommonComponent): DataUnitComponent
    }

    public abstract val unitPreferences: UnitPreferences
}
