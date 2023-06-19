@file:Suppress("LongMethod")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind

@Preview
@Composable
private fun CarPreview() {
    CurrentVehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent()
    )
}

@Preview
@Composable
private fun SingleAxleTrailerPreview() {
    CurrentVehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.SINGLE_AXLE_TRAILER)
            )
        )
    )
}

@Preview
@Composable
private fun MotorcyclePreview() {
    CurrentVehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.MOTORCYCLE)
            )
        )
    )
}

@Preview
@Composable
private fun TadpoleThreadWheelerPreview() {
    CurrentVehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.TADPOLE_THREE_WHEELER)
            )
        )
    )
}

@Preview
@Composable
private fun DeltaThreadWheelerPreview() {
    CurrentVehicle(
        modifier = Modifier.fillMaxSize(),
        viewModel = previewCurrentVehicleComponent(
            PreviewVehicleComponent(
                vehicle = previewVehicle.copy(kind = Kind.DELTA_THREE_WHEELER)
            )
        )
    )
}
