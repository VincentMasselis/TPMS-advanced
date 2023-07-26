package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Preview
@Composable
private fun TyrePreview() {
    LazyRow {
        items(previewTyreViewModelStates) {
            Tyre(
                location = Location.Wheel(SensorLocation.REAR_RIGHT),
                vehicleComponent = PreviewVehicleComponent(),
                viewModel = previewTyreViewModelImpl(it),
            )
        }
    }
}
