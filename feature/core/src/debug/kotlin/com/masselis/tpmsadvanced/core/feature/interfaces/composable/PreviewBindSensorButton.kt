package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel.State
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Preview
@Composable
private fun BindSensorButtonNewBindingPreview() {
    BindSensorButton(
        location = Location.Wheel(SensorLocation.REAR_LEFT),
        vehicleComponent = PreviewVehicleComponent(),
        viewModel = previewBindSensorViewModel(State.RequestBond.NewBinding(previewSensor))
    )
}

@Preview
@Composable
private fun BindSensorButtonAlreadyBoundPreview() {
    BindSensorButton(
        location = Location.Wheel(SensorLocation.REAR_LEFT),
        vehicleComponent = PreviewVehicleComponent(),
        viewModel = previewBindSensorViewModel(
            State.RequestBond.AlreadyBound(
                previewSensor,
                previewVehicle,
                previewVehicle
            )
        )
    )
}
