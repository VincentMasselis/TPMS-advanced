package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.text.format.DateUtils.getRelativeTimeSpanString
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel.Event
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel.State
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleBindings.Companion.SwapFrontRearViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Factory.Companion.key
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ColumnScope.SwapFrontRearButton(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    component: VehicleComponent = LocalVehicleComponent.current,
    viewModel: SwapFrontRearViewModel = component.viewModel(component.key()) {
        it.SwapFrontRearViewModel()
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    SwapFrontRearButton(
        state = state,
        swap = viewModel::swap,
        modifier = modifier
    )
    LaunchedEffect(viewModel.eventChannel) {
        for (event in viewModel.eventChannel) {
            when (event) {
                Event.SensorsSwapped -> {
                    snackbarHostState.showSnackbar(
                        "Front and rear sensors are swapped",
                        withDismissAction = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SwapFrontRearButton(
    state: State,
    swap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is State.Unavailable) return
    Separator()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.align(Alignment.End)
    ) {
        if (state is State.LastUsage) {
            val now by produceState(now().toLocalDateTime(currentSystemDefault())) {
                while (true) {
                    delay(1.seconds)
                    value = now().toLocalDateTime(currentSystemDefault())
                }
            }
            Text(
                text = state.instant
                    .toLocalDateTime(currentSystemDefault())
                    .elapsedUntil(now)
                    .let { "Last used: %s".format(it) },
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.width(8.dp))
        }
        OutlinedButton(onClick = swap) {
            Text(text = "Swap front/rear sensors")
        }
    }
}

public fun LocalDateTime.elapsedUntil(
    other: LocalDateTime,
    timeZone: TimeZone = currentSystemDefault(),
): String = getRelativeTimeSpanString(
    toInstant(timeZone).toEpochMilliseconds(),
    other.toInstant(timeZone).toEpochMilliseconds(),
    SECOND_IN_MILLIS,
).toString()

@Preview(showBackground = true)
@Composable
private fun SwapFrontRearButton10SecondsAgoPreview() {
    Column {
        SwapFrontRearButton(
            state = State.LastUsage(now().minus(10.seconds)),
            swap = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwapFrontRearButton10MinutesAgoPreview() {
    Column {
        SwapFrontRearButton(
            state = State.LastUsage(now().minus(10.minutes)),
            swap = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwapFrontRearButtonYesterdayPreview() {
    Column {
        SwapFrontRearButton(
            state = State.LastUsage(now().minus(1.days)),
            swap = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwapFrontRearButton1MonthAgoPreview() {
    Column {
        SwapFrontRearButton(
            state = State.LastUsage(now().minus(32.days)),
            swap = {}
        )
    }
}
