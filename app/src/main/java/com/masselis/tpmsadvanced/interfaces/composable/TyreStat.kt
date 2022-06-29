package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.mock.mocks
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
    viewModel: TyreStatsViewModel = savedStateViewModel(key = "TyreStatsViewModel_${location.name}") {
        location.component.tyreStatViewModelFactory.build(it)
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    val (pressure, temperature) = when (val state = state) {
        State.NotDetected -> null to null
        is State.Normal -> state.pressure to state.temperature
        is State.Alerting -> state.pressure to state.temperature
        is State.Obsolete -> state.pressure to state.temperature
    }
    val color = when (state) {
        State.NotDetected, is State.Obsolete, is State.Normal -> MaterialTheme.colorScheme.onSurface
        is State.Alerting -> MaterialTheme.colorScheme.error
    }
    val isVisible = remember { mutableStateOf(true) }
    if (state is State.Alerting) {
        LaunchedEffect(key1 = isVisible) {
            launch {
                repeat(Int.MAX_VALUE) {
                    delay(300.milliseconds)
                    isVisible.value = !isVisible.value
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
        modifier = modifier
            .requiredWidthIn(0.dp, 75.dp)
            .alpha(if (isVisible.value) 1f else 0f)
    ) {
        Text(
            pressure?.bar?.let { "%.2f bar".format(it) } ?: "-.--",
            modifier = Modifier
                .align(alignment),
            maxLines = 1,
            color = color,
        )
        Text(
            temperature?.celsius?.let { "%.1f°C".format(it) } ?: "-.-",
            modifier = Modifier
                .align(alignment),
            maxLines = 1,
            fontSize = 16.sp,
            color = color
        )
    }
}

@Preview
@Composable
fun TyreStatPreview() {
    LazyColumn {
        items(TyreStatsViewModel.mocks) {
            TyreStat(
                location = FRONT_LEFT,
                modifier = Modifier.width(350.dp),
                viewModel = it
            )
        }
    }
}