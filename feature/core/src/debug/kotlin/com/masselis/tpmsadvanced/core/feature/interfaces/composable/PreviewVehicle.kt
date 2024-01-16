@file:Suppress("LongMethod")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind

@Preview
@Composable
private fun CarPreview() {
    CompositionLocalProvider(LocalVehicleComponent provides PreviewVehicleComponent()) {
        CurrentVehicle(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun SingleAxleTrailerPreview() {
    CompositionLocalProvider(
        LocalVehicleComponent provides PreviewVehicleComponent(
            vehicle = previewVehicle.copy(kind = Kind.SINGLE_AXLE_TRAILER)
        )
    ) {
        CurrentVehicle(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun MotorcyclePreview() {
    CompositionLocalProvider(
        LocalVehicleComponent provides PreviewVehicleComponent(
            vehicle = previewVehicle.copy(kind = Kind.MOTORCYCLE)
        )
    ) {
        CurrentVehicle(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun TadpoleThreadWheelerPreview() {
    CompositionLocalProvider(
        LocalVehicleComponent provides PreviewVehicleComponent(
            vehicle = previewVehicle.copy(kind = Kind.TADPOLE_THREE_WHEELER)
        )
    ) {
        CurrentVehicle(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun DeltaThreadWheelerPreview() {
    CompositionLocalProvider(
        LocalVehicleComponent provides PreviewVehicleComponent(
            vehicle = previewVehicle.copy(kind = Kind.DELTA_THREE_WHEELER)
        )
    ) {
        CurrentVehicle(modifier = Modifier.fillMaxSize())
    }
}
