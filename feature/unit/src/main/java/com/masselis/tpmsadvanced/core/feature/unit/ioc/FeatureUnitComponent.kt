package com.masselis.tpmsadvanced.core.feature.unit.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.UnitsViewModel
import dagger.Component

@SingleInstance
@Component(
    dependencies = [
        DataUnitComponent::class
    ]
)
public abstract class FeatureUnitComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(dataUnitComponent: DataUnitComponent): FeatureUnitComponent
    }

    internal abstract val unitsViewModel: UnitsViewModel
}
