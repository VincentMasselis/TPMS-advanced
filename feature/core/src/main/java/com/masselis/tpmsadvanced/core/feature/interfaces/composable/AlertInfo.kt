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
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorDialogViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DemoTyreViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.*

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
                    location = SensorLocation.FRONT_LEFT,
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
                    location = SensorLocation.FRONT_LEFT,
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

private class DemoVehicleComponent : VehicleComponent() {
    override val findTyreComponentUseCase: FindTyreComponentUseCase
        get() = TODO("Not yet implemented")
    override val carId: UUID
        get() = TODO("Not yet implemented")
    override val carFlow: Flow<Vehicle>
        get() = TODO("Not yet implemented")
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
