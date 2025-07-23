package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.CurrentVehicleDropdownViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dagger.Component

public interface FeatureMainComponent {

    @javax.inject.Scope
    public annotation class Scope

    public val currentVehicleUseCase: CurrentVehicleUseCase
    public val noveltyUseCase: NoveltyUseCase
    public val vehicleListUseCase: VehicleListUseCase

    public companion object : FeatureMainComponent by InternalComponent
}

@Suppress("PropertyName", "VariableNaming")
@FeatureMainComponent.Scope
@Component(
    modules = [
        VehicleSubcomponentModule::class
    ],
    dependencies = [
        CoreCommonComponent::class,
        DataUnitComponent::class,
        DataVehicleComponent::class,
        DataAppComponent::class,
    ]
)
internal interface InternalComponent : FeatureMainComponent {

    fun PreconditionsViewModel(): PreconditionsViewModel

    val CurrentVehicleDropdownViewModel: CurrentVehicleDropdownViewModelImpl.Factory

    val vehicleComponentCacheUseCase: VehicleComponentCacheUseCase

    companion object : InternalComponent by DaggerInternalComponent
        .builder()
        .coreCommonComponent(CoreCommonComponent)
        .dataUnitComponent(DataUnitComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .dataAppComponent(DataAppComponent)
        .build()
}
