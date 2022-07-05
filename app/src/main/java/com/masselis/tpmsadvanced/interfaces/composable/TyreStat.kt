package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.model.TyreLocation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Suppress("NAME_SHADOWING")
@Composable
fun TyreStat(
    modifier: Modifier = Modifier,
    location: TyreLocation,
    viewModel: TyreStatsViewModel = viewModel(key = "TyreStatsViewModel_${location.name}") {
        location.component.tyreStatViewModelFactory.build(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    val (pressure, temperature) = when (val state = state) {
        State.NotDetected -> null to null
        is State.Normal -> state.pressure to state.temperature
        is State.Alerting -> state.pressure to state.temperature
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
    }
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
            pressure?.asBar()?.let { "%.2f bar".format(it) } ?: "-.--",
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            color = color,
            modifier = Modifier.align(alignment),
        )
        Text(
            temperature?.celsius?.let { "%.1f°C".format(it) } ?: "-.-",
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            fontSize = 16.sp,
            color = color,
            modifier = Modifier.align(alignment),
        )
    }
}