package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_RIGHT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Suppress("NAME_SHADOWING")
@Composable
internal fun TyreStat(
    location: TyreLocation,
    modifier: Modifier = Modifier,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: TyreStatsViewModel = viewModel(key = "TyreStatsViewModel_${carComponent.car.uuid}_${location.name}") {
        carComponent.tyreComponent(location).tyreStatViewModelFactory
            .build(createSavedStateHandle())
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
    val alignment by remember {
        derivedStateOf {
            when (location) {
                FRONT_LEFT, REAR_LEFT -> Alignment.End
                FRONT_RIGHT, REAR_RIGHT -> Alignment.Start
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
