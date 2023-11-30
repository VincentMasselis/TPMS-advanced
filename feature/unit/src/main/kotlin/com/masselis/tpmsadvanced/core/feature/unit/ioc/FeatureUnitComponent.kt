package com.masselis.tpmsadvanced.core.feature.unit.ioc

import com.masselis.tpmsadvanced.core.feature.unit.interfaces.UnitsViewModel
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component

@FeatureUnitComponent.Scope
@Component(
    dependencies = [
        DataUnitComponent::class
    ]
)
internal interface FeatureUnitComponent {
    @javax.inject.Scope
    annotation class Scope

    fun UnitsViewModel(): UnitsViewModel

    companion object : FeatureUnitComponent by DaggerFeatureUnitComponent
        .builder()
        .dataUnitComponent(DataUnitComponent)
        .build()
}
