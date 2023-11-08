package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent.Companion.vehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Named

@Suppress("PropertyName", "VariableNaming")
@VehicleComponent.Scope
@Subcomponent(
    modules = [
        VehicleModule::class,
        TyreSubComponentModule::class
    ]
)
public abstract class VehicleComponent {

    @Subcomponent.Factory
    public abstract class Factory protected constructor() {

        @InternalDaggerImplementation
        internal abstract fun daggerOnlyBuild(
            @BindsInstance @Named("base") vehicle: Vehicle,
        ): VehicleComponent

        public fun build(vehicle: Vehicle): VehicleComponent =
            vehicleComponentCacheUseCase.find(vehicle)
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    public abstract val vehicle: Vehicle
    public abstract val carFlow: StateFlow<Vehicle>

    public abstract val vehicleRangesUseCase: VehicleRangesUseCase
    public abstract val tyreComponent: FindTyreComponentUseCase

    internal abstract val ClearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract fun VehicleSettingsViewModel(): VehicleSettingsViewModel
    internal abstract fun DeleteVehicleViewModel(): DeleteVehicleViewModel
}
