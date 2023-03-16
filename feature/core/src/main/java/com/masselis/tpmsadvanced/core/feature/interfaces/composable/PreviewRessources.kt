package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

internal fun noMock(): Nothing = throw IllegalStateException("No mock available")

internal val previewVehicle = Vehicle(
    UUID.randomUUID(),
    Vehicle.Kind.CAR,
    "PREVIEW",
    0.8f.bar,
    2.5f.bar,
    5f.celsius,
    40f.celsius,
    90f.celsius
)

internal class PreviewTyreComponent(
    private val lazyTyreViewModelFactory: Lazy<TyreViewModelImpl.Factory>,
    private val lazyTyreStatViewModelFactory: Lazy<TyreStatsViewModel.Factory>,
    private val lazyBindSensorButtonViewModelFactory: Lazy<BindSensorButtonViewModel.Factory>
) : TyreComponent() {
    override val tyreViewModelFactory: TyreViewModelImpl.Factory
        get() = lazyTyreViewModelFactory.value
    override val tyreStatViewModelFactory: TyreStatsViewModel.Factory
        get() = lazyTyreStatViewModelFactory.value
    override val bindSensorButtonViewModelFactory: BindSensorButtonViewModel.Factory
        get() = lazyBindSensorButtonViewModelFactory.value
}

internal class PreviewVehicleComponent(
    private val lazyFindTyreComponentUseCase: Lazy<FindTyreComponentUseCase> = lazy { noMock() },
    private val lazyVehicle: Lazy<Vehicle> = lazy { previewVehicle },
    private val lazyCarFlow: Lazy<StateFlow<Vehicle>> = lazy { MutableStateFlow(lazyVehicle.value) },
    private val lazyScope: Lazy<CoroutineScope> = lazy { noMock() },
    private val lazyClearBoundSensorsViewModel: Lazy<ClearBoundSensorsViewModel.Factory>,
    private val lazyVehicleSettingsViewModel: VehicleSettingsViewModel.Factory,
    private val lazyBindSensorDialogViewModelFactory: BindSensorDialogViewModel.Factory,
    private val lazyDeleteVehicleViewModel: DeleteVehicleViewModel,
    private val lazyDeleteVehicleAlertViewModel: DeleteVehicleAlertViewModel
) : VehicleComponent() {
    override val findTyreComponentUseCase: FindTyreComponentUseCase
        get() = lazyFindTyreComponentUseCase.value
    override val vehicle: Vehicle
        get() = lazyVehicle.value
    override val carFlow: StateFlow<Vehicle>
        get() = lazyCarFlow.value
    override val scope: CoroutineScope
        get() = TODO("Not yet implemented")
    override val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
        get() = TODO("Not yet implemented")
    override val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory
        get() = TODO("Not yet implemented")
    override val bindSensorDialogViewModelFactory: BindSensorDialogViewModel.Factory
        get() = TODO("Not yet implemented")
    override val deleteVehicleViewModel: DeleteVehicleViewModel
        get() = TODO("Not yet implemented")
    override val deleteVehicleAlertViewModel: DeleteVehicleAlertViewModel
        get() = TODO("Not yet implemented")
}