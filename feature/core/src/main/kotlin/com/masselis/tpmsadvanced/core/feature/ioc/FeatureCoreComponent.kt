package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.CurrentVehicleDropdownViewModelImpl
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import dagger.Component

public interface FeatureCoreComponent {

    @javax.inject.Scope
    public annotation class Scope

    public val currentVehicleUseCase: CurrentVehicleUseCase
    public val noveltyUseCase: NoveltyUseCase
    public val vehicleListUseCase: VehicleListUseCase

    public companion object : FeatureCoreComponent by InternalComponent
}

@Suppress("PropertyName")
@FeatureCoreComponent.Scope
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
internal interface InternalComponent : FeatureCoreComponent {

    fun PreconditionsViewModel(): PreconditionsViewModel

    @Suppress("VariableNaming")
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
