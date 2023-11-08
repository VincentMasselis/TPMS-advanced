package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Suppress("NAME_SHADOWING", "LongMethod", "CyclomaticComplexMethod")
@Composable
internal fun TyreStat(
    location: Location,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: TyreStatsViewModel = viewModel(
        key = "TyreStatsViewModel_${vehicleComponent.vehicle.uuid}_${location}"
    ) {
        vehicleComponent
            .tyreComponent(location)
            .TyreStatViewModel(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    val (pressure, temperature) = when (val state = state) {
        State.NotDetected -> null to null
        is State.Normal -> Pair(
            Pair(state.pressure, state.pressureUnit),
            Pair(state.temperature, state.temperatureUnit)
        )

        is State.Alerting -> Pair(
            Pair(state.pressure, state.pressureUnit),
            Pair(state.temperature, state.temperatureUnit)
        )
    }
    val color = when (state) {
        State.NotDetected, is State.Normal -> MaterialTheme.colorScheme.onSurface
        is State.Alerting -> MaterialTheme.colorScheme.error
    }
    var isVisible by remember { mutableStateOf(true) }
    if (state is State.Alerting) {
        LaunchedEffect(key1 = isVisible) {
            launch {
                repeat(Int.MAX_VALUE) {
                    delay(300.milliseconds)
                    isVisible = !isVisible
                }
            }
        }
    } else
        isVisible = true
    val alignment = remember {
        when (location) {
            is Location.Axle -> Alignment.Start
            is Location.Wheel -> when (location.location.side) {
                LEFT -> Alignment.End
                RIGHT -> Alignment.Start
            }

            is Location.Side -> when (location.side) {
                LEFT -> Alignment.End
                RIGHT -> Alignment.Start
            }
        }
    }
    Column(
        modifier = modifier.alpha(if (isVisible) 1f else 0f)
    ) {
        Text(
            pressure?.let { (value, unit) -> value.string(unit) } ?: "-.--",
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            color = color,
            modifier = Modifier.align(alignment),
        )
        Text(
            temperature?.let { (value, unit) -> value.string(unit) } ?: "-.-",
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            fontSize = 16.sp,
            color = color,
            modifier = Modifier.align(alignment),
        )
    }
}
