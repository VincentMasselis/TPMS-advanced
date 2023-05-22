package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
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
internal abstract class VehicleComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance @Named("base") vehicle: Vehicle): VehicleComponent
    }

    @javax.inject.Scope
    annotation class Scope

    protected abstract val findTyreComponentUseCase: FindTyreComponentUseCase

    fun tyreComponent(sensors: ManySensor) = when (sensors) {
        is ManySensor.Located -> findTyreComponentUseCase.find(sensors.location)
        is ManySensor.Axle -> findTyreComponentUseCase.find(sensors.axle)
        is ManySensor.Side -> findTyreComponentUseCase.find(sensors.side)
    }

    @get:Named("base")
    abstract val vehicle: Vehicle
    abstract val carFlow: StateFlow<Vehicle>
    abstract val scope: CoroutineScope

    abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    abstract val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
    abstract val deleteVehicleViewModel: DeleteVehicleViewModel.Factory
}
