package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.Component

@DataUnitComponent.Scope
@Component(
    dependencies = [CoreCommonComponent::class]
)
public interface DataUnitComponent {
    @Component.Factory
    public abstract class Factory {
        internal abstract fun build(coreCommonComponent: CoreCommonComponent): DataUnitComponent
    }

    public val unitPreferences: UnitPreferences

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataUnitComponent by DaggerDataUnitComponent
        .factory()
        .build(CoreCommonComponent)
}
