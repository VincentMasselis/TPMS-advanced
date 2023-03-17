package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.RIGHT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Preview
@Composable
private fun TyreStatPreview() {
    LazyRow {
        items(previewTyreStatViewModelStates) {
            TyreStat(
                manySensor = ManySensor.Located(SensorLocation.REAR_RIGHT),
                vehicleComponent = PreviewVehicleComponent(),
                viewModel = previewTyreStatViewModel(it),
            )
        }
    }
}
