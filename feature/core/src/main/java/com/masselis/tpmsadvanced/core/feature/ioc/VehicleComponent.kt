package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.BindsInstance
import dagger.Lazy
import dagger.Subcomponent
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Named

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

        @Inject
        internal lateinit var componentsUseCase: Lazy<VehicleComponentCacheUseCase>

        init {
            @Suppress("LeakingThis")
            FeatureCoreComponent.inject(this)
        }

        @InternalDaggerImplementation
        internal abstract fun daggerOnlyBuild(
            @BindsInstance @Named("base") vehicle: Vehicle,
        ): VehicleComponent

        public fun build(vehicle: Vehicle): VehicleComponent = componentsUseCase.get().find(vehicle)
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    public abstract val vehicle: Vehicle
    public abstract val carFlow: StateFlow<Vehicle>

    public abstract val vehicleRangesUseCase: VehicleRangesUseCase
    public abstract val tyreComponent: FindTyreComponentUseCase

    internal abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
    internal abstract val deleteVehicleViewModel: DeleteVehicleViewModel.Factory
}
