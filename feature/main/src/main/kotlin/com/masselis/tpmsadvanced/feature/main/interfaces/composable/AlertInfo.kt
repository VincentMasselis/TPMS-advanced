package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.InternalTyreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun PressureInfo(
    pressureRange: ClosedFloatingPointRange<Pressure>,
    unit: PressureUnit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Tyre(
                    location = Location.Side(SensorLocation.Side.LEFT),
                    snackbarHostState = DemoSnackbarHostState,
                    modifier = Modifier.height(150.dp),
                    vehicleComponent = DemoVehicleComponent(),
                    viewModel = DemoTyreViewModel(State.Alerting),
                )
                Text(
                    "If the pressure is below %s or above %s, the tyre starts to blink in red to alert you"
                        .format(
                            pressureRange.start.string(unit),
                            pressureRange.endInclusive.string(unit)
                        )
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

@Composable
internal fun TemperatureInfo(
    text: String,
    state: State,
    temperature: Temperature,
    unit: TemperatureUnit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val demoTyreViewModel = remember(state) { DemoTyreViewModel(state) }
                Tyre(
                    location = Location.Side(SensorLocation.Side.LEFT),
                    snackbarHostState = DemoSnackbarHostState,
                    modifier = Modifier.height(150.dp),
                    vehicleComponent = DemoVehicleComponent(),
                    viewModel = demoTyreViewModel,
                )
                Text(text.format(temperature.string(unit)))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

private class DemoTyreViewModel(state: State) : TyreViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)
}

private class DemoVehicleComponent : InternalVehicleComponent {
    override val TyreComponent: FindTyreComponentUseCase
        get() = error("Not implemented")
    override val tyreFactory: InternalTyreComponent.Factory
        get() = error("Not implemented")
    override val vehicle: Vehicle
        get() = error("Not implemented")
    override val vehicleStateFlow: StateFlow<Vehicle>
        get() = error("Not implemented")
    override val vehicleRangesUseCase: VehicleRangesUseCase
        get() = error("Not implemented")
    override val ClearBoundSensorsViewModel: ClearBoundSensorsViewModelImpl.Factory
        get() = error("Not implemented")

    override fun VehicleSettingsViewModel(): VehicleSettingsViewModelImpl =
        error("Not implemented")

    override fun DeleteVehicleViewModel(): DeleteVehicleViewModelImpl =
        error("Not implemented")
}

private val DemoSnackbarHostState = SnackbarHostState()
