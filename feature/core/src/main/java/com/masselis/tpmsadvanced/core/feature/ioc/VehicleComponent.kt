package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import java.util.*
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
    public interface Factory {
        public fun build(
            @BindsInstance @Named("base") vehicle: Vehicle,
            @BindsInstance scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        ): VehicleComponent
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    public abstract val vehicle: Vehicle
    public abstract val carFlow: StateFlow<Vehicle>
    public abstract val scope: CoroutineScope

    public abstract val vehicleRangesUseCase: VehicleRangesUseCase
    public abstract val findTyreComponentUseCase: FindTyreComponentUseCase

    internal abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
    internal abstract val deleteVehicleViewModel: DeleteVehicleViewModel.Factory
}
