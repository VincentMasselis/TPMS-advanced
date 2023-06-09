package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.ActiveVehicleComponentsUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.BindsInstance
import dagger.Lazy
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
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
        internal lateinit var componentsUseCase: Lazy<ActiveVehicleComponentsUseCase>

        init {
            @Suppress("LeakingThis")
            FeatureCoreComponent.inject(this)
        }

        @InternalDaggerImplementation
        internal abstract fun daggerBuild(
            @BindsInstance @Named("base") vehicle: Vehicle,
        ): VehicleComponent

        public fun build(vehicle: Vehicle): VehicleComponent = componentsUseCase.get().hold(vehicle)
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("vehicle_component")
    public abstract val release: () -> Unit


    @get:Named("base")
    public abstract val vehicle: Vehicle
    public abstract val carFlow: StateFlow<Vehicle>

    @get:Named("vehicle_component")
    public abstract val scope: CoroutineScope

    public abstract val vehicleRangesUseCase: VehicleRangesUseCase
    public abstract val findTyreComponentUseCase: FindTyreComponentUseCase

    internal abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
    internal abstract val deleteVehicleViewModel: DeleteVehicleViewModel.Factory
}
