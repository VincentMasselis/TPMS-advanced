package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreBindings.Companion.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent.Companion.TyreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent.Companion.keyed
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import java.text.SimpleDateFormat
import java.util.Date

@Composable
internal fun TyreStat(
    location: Location,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: TyreStatsViewModel = vehicleComponent
        .TyreComponent(location)
        .let { viewModel(it.keyed()) { it.TyreStatsViewModel() } },
) {
    val state by viewModel.stateFlow.collectAsState()
    val showTimestamp by viewModel.showTimestamp.collectAsState()
    val showSensorId by viewModel.showSensorId.collectAsState()
    val showTimeSinceUpdate by viewModel.showTimeSinceUpdate.collectAsState()
    TyreStat(location, state, showTimestamp, showSensorId, showTimeSinceUpdate, modifier)
}

@Suppress("NAME_SHADOWING", "LongMethod", "CyclomaticComplexMethod", "ComplexCondition")
@Composable
private fun TyreStat(
    location: Location,
    state: State,
    showTimestamp: Boolean = false,
    showSensorId: Boolean = false,
    showTimeSinceUpdate: Boolean = true,
    modifier: Modifier = Modifier,
) {
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
    val sensorId = when (state) {
        State.NotDetected -> null
        is State.Normal -> state.sensorId
        is State.Alerting -> state.sensorId
    }
    val timestamp = when (state) {
        State.NotDetected -> null
        is State.Normal -> state.timestamp
        is State.Alerting -> state.timestamp
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

        if (showTimeSinceUpdate && timestamp != null) {
            Text(
                elapsedSinceUpdateLabel(timestamp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                fontSize = 16.sp,
                color = color,
                modifier = Modifier.align(alignment),
            )
        }

        if (sensorId != null && timestamp != null && (showSensorId || showTimestamp)) {
            val displaySensorId = if (showSensorId) {
                if ((sensorId ushr 24) == 0) {
                    "%02X%02X%02X".format(
                        sensorId and 0xFF,
                        (sensorId shr 8) and 0xFF,
                        (sensorId shr 16) and 0xFF,
                    )
                } else {
                    "0x%08X".format(sensorId)
                }
            } else null

            val lastReceived = if (showTimestamp) {
                val locale = LocalLocale.current.platformLocale
                SimpleDateFormat("dd/MM/yyyy h:mma", locale)
                    .format(Date((timestamp * 1000).toLong()))
                    .lowercase(locale)
                    .let { "Last: $it" }
            } else null

            Text(
                text = listOfNotNull(displaySensorId, lastReceived).joinToString("  "),
                fontSize = 9.sp,
                maxLines = 1,
                color = color,
                modifier = Modifier.align(alignment),
            )
        }
    }
}

// Re-emits on every tier boundary crossed (each minute, then each hour, then each day) so the
// label stays live without waiting for a new sensor packet. A new `timestamp` (new packet)
// restarts this from scratch via the `key1` change, cancelling any pending delay.
@Composable
private fun elapsedSinceUpdateLabel(timestamp: Double): String {
    val label by produceState(initialValue = elapsedLabel(timestamp, now()), key1 = timestamp) {
        // produceState's underlying mutableStateOf survives across key1 changes, only the
        // producer coroutine restarts - so `value` must be set immediately here (not after the
        // first delay) or a new packet would leave the stale label showing until the next tick.
        while (true) {
            value = elapsedLabel(timestamp, now())
            delay(nextElapsedTick(timestamp, now()))
        }
    }
    return label
}

private fun elapsedLabel(timestamp: Double, now: Double): String {
    val totalMinutes = totalMinutesSince(timestamp, now)
    val totalHours = totalMinutes / MINUTES_PER_HOUR
    return when {
        totalMinutes == 0L -> "<1 min"
        totalMinutes < HOUR_TIER_START_MINUTES -> "$totalMinutes min"
        totalHours < DAY_TIER_START_HOURS -> "$totalHours hour"
        else -> "${totalHours / HOURS_PER_DAY} days"
    }
}

// Delay until the label's next tier boundary: every minute while under 2 hours, every hour
// while under 2 days, every day after that.
private fun nextElapsedTick(timestamp: Double, now: Double): Duration {
    val totalMinutes = totalMinutesSince(timestamp, now)
    val totalHours = totalMinutes / MINUTES_PER_HOUR
    val nextBoundarySeconds = when {
        totalMinutes < HOUR_TIER_START_MINUTES ->
            (totalMinutes + 1) * SECONDS_PER_MINUTE

        totalHours < DAY_TIER_START_HOURS ->
            (totalHours + 1) * MINUTES_PER_HOUR * SECONDS_PER_MINUTE

        else ->
            (totalHours / HOURS_PER_DAY + 1) * HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
    }
    return (timestamp + nextBoundarySeconds - now).seconds.coerceAtLeast(Duration.ZERO)
}

private fun totalMinutesSince(timestamp: Double, now: Double): Long =
    ((now - timestamp) / SECONDS_PER_MINUTE).toLong().coerceAtLeast(0L)

private const val SECONDS_PER_MINUTE = 60L
private const val MINUTES_PER_HOUR = 60L
private const val HOURS_PER_DAY = 24L
private const val HOUR_TIER_START_MINUTES = 120L
private const val DAY_TIER_START_HOURS = 48L


@Preview
@Composable
internal fun TyreStatNotDetectedPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state = State.NotDetected,
        showTimeSinceUpdate = false,
    )
}


@Preview
@Composable
internal fun TyreStatNormalPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state =
            State.Normal(
                0.0,
                0,
                2f.bar,
                PressureUnit.BAR,
                30f.celsius,
                TemperatureUnit.CELSIUS
            ),
        showTimeSinceUpdate = false,
    )
}


@Preview
@Composable
internal fun TyreStatAlertingPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state = State.Alerting(
            0.0,
            0,
            0.5f.bar,
            PressureUnit.BAR,
            150f.celsius,
            TemperatureUnit.CELSIUS
        ),
        showTimeSinceUpdate = false,
    )
}


@Preview
@Composable
internal fun TyreStatTimeSinceUpdateMinutesPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state = State.Normal(
            now() - 5 * SECONDS_PER_MINUTE,
            0,
            2f.bar,
            PressureUnit.BAR,
            30f.celsius,
            TemperatureUnit.CELSIUS
        ),
        showTimeSinceUpdate = true,
    )
}


@Preview
@Composable
internal fun TyreStatTimeSinceUpdateHoursPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state = State.Normal(
            now() - 5 * MINUTES_PER_HOUR * SECONDS_PER_MINUTE,
            0,
            2f.bar,
            PressureUnit.BAR,
            30f.celsius,
            TemperatureUnit.CELSIUS
        ),
        showTimeSinceUpdate = true,
    )
}


@Preview
@Composable
internal fun TyreStatTimeSinceUpdateDaysPreview() {
    TyreStat(
        location = Location.Wheel(SensorLocation.REAR_RIGHT),
        state = State.Normal(
            now() - 5 * HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE,
            0,
            2f.bar,
            PressureUnit.BAR,
            30f.celsius,
            TemperatureUnit.CELSIUS
        ),
        showTimeSinceUpdate = true,
    )
}
