package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@Suppress("PropertyName", "VariableNaming")
@VehicleComponent.Scope
@Subcomponent(
    modules = [VehicleModule::class, TyreSubComponentModule::class]
)
public abstract class VehicleComponent {

    @Subcomponent.Factory
    internal abstract class Factory protected constructor() {
        internal abstract fun build(@BindsInstance @Named("base") vehicle: Vehicle): VehicleComponent
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    public abstract val vehicle: Vehicle
    public abstract val carFlow: StateFlow<Vehicle>

    public abstract val vehicleRangesUseCase: VehicleRangesUseCase
    public abstract val TyreComponent: FindTyreComponentUseCase

    internal abstract val ClearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract fun VehicleSettingsViewModel(): VehicleSettingsViewModel
    internal abstract fun DeleteVehicleViewModel(): DeleteVehicleViewModel

    public companion object : Injectable()

    @Suppress("UnnecessaryAbstractClass")
    public abstract class Injectable protected constructor() : (Vehicle) -> VehicleComponent {

        @Inject
        internal lateinit var factory: Factory

        @Inject
        internal lateinit var vehicleComponentCacheUseCase: VehicleComponentCacheUseCase

        init {
            @Suppress("LeakingThis")
            FeatureCoreComponent.inject(this)
        }

        override fun invoke(vehicle: Vehicle): VehicleComponent =
            vehicleComponentCacheUseCase.find(vehicle) { factory.build(vehicle) }
    }
}
