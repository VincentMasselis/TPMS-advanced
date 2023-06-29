package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Preview
@Composable
private fun TyreStatPreview() {
    LazyRow {
        items(previewTyreStatViewModelStates) {
            TyreStat(
                location = Location.Wheel(SensorLocation.REAR_RIGHT),
                vehicleComponent = PreviewVehicleComponent(),
                viewModel = previewTyreStatViewModel(it),
            )
        }
    }
}
