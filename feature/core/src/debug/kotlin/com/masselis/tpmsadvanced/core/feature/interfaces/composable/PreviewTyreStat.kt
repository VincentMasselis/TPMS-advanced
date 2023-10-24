package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location

@Preview
@Composable
private fun TyreStatPreview() {
    LazyRow {
        items(previewTyreStatViewModelStates) {
            TyreStat(
                location = Location.Wheel(REAR_RIGHT),
                vehicleComponent = PreviewVehicleComponent(),
                viewModel = previewTyreStatViewModel(it),
            )
        }
    }
}
