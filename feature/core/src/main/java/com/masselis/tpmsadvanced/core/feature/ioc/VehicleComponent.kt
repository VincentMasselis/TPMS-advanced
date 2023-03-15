package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorDialogViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.*

@VehicleComponent.Scope
@Subcomponent(
    modules = [
        VehicleModule::class,
        TyreComponentModule::class
    ]
)
internal abstract class VehicleComponent {
    @Subcomponent.Factory
    internal interface Factory {
        fun build(@BindsInstance carId: UUID): VehicleComponent
    }

    @Scope
    internal annotation class Scope

    protected abstract val findTyreComponentUseCase: FindTyreComponentUseCase
    internal fun tyreComponent(location: SensorLocation) = findTyreComponentUseCase.find(location)

    internal abstract val carId: UUID
    internal abstract val carFlow: Flow<Vehicle>
    internal abstract val scope: CoroutineScope

    internal abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
    internal abstract val bindSensorDialogViewModelFactory: BindSensorDialogViewModel.Factory
    internal abstract val deleteVehicleViewModel: DeleteVehicleViewModel
    internal abstract val deleteVehicleAlertViewModel: DeleteVehicleAlertViewModel
}
