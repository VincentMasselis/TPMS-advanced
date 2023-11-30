package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DemoTyreViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.InternalVehicleComponent
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
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
                Tyre(
                    location = Location.Side(SensorLocation.Side.LEFT),
                    modifier = Modifier.height(150.dp),
                    vehicleComponent = DemoVehicleComponent(),
                    viewModel = DemoTyreViewModel(state),
                )
                Text(text.format(temperature.string(unit)))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

private class DemoVehicleComponent : InternalVehicleComponent {
    override val TyreComponent: (Location) -> TyreComponent
        get() = error("Not implemented")
    override val InternalTyreComponent: FindTyreComponentUseCase
        get() = error("Not implemented")
    override val vehicle: Vehicle
        get() = error("Not implemented")
    override val carFlow: StateFlow<Vehicle>
        get() = error("Not implemented")
    override val vehicleRangesUseCase: VehicleRangesUseCase
        get() = error("Not implemented")
    override val ClearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
        get() = error("Not implemented")

    override fun VehicleSettingsViewModel(): VehicleSettingsViewModel =
        error("Not implemented")

    override fun DeleteVehicleViewModel(): DeleteVehicleViewModel =
        error("Not implemented")
}
