package com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui

import android.content.Intent
import android.icu.text.DateFormat.SHORT
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.ui.restartApp
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.appendLoc
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.UnlocatedSensorListTags.bindingFinishedGoBackButton
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.UnlocatedSensorListTags.clearBindingsAndContinueButton
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.UnlocatedSensorListTags.sensorUnpluggedButton
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModel
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.feature.unlocated.ioc.FeatureUnlocatedComponent.Companion.ListSensorViewModel
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Composable
public fun UnlocatedSensorList(
    vehicleUuid: UUID,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Content(
        vehicleUuid = vehicleUuid,
        bindingFinished = bindingFinished,
        modifier = modifier,
    )
}

@Suppress("NAME_SHADOWING")
@Composable
private fun Content(
    vehicleUuid: UUID,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListSensorViewModel = viewModel { ListSensorViewModel(vehicleUuid) }
) {
    val state by viewModel.stateFlow.collectAsState()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(end = 16.dp, start = 16.dp)
            .testTag(UnlocatedSensorListTags.root)
    ) {
        when (val state = state) {
            is State.AllWheelsAreAlreadyBound -> AllWheelsAreAlreadyBound(
                kind = state.kind,
                onAcknowledge = viewModel::acknowledgeAndClearBinding
            )

            State.UnplugEverySensor -> UnplugEverySensor(
                onAcknowledge = viewModel::acknowledgeSensorUnplugged,
            )

            is State.Search -> Searching(
                state = state,
                vehicleUUID = vehicleUuid,
                bindingFinished = bindingFinished,
            )

            State.Issue -> Issue()
        }
    }
}

@Composable
private fun AllWheelsAreAlreadyBound(
    kind: Vehicle.Kind,
    onAcknowledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Vehicle(
            kind = kind,
            states = kind.locations.associateWith { WheelState.Fade }.toPersistentMap(),
            modifier = Modifier.height(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Each tyre of your vehicle is already bound to a sensor,\ndo your really want to continue ?",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAcknowledge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag(clearBindingsAndContinueButton)
        ) {
            Text("Clear bindings and continue")
        }
    }
}

@Composable
private fun UnplugEverySensor(
    onAcknowledge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = "Before starting, please unplug all sensors you intend to bind from your tyres.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAcknowledge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag(sensorUnpluggedButton)
        ) {
            Text("Sensors unplugged")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Searching(
    state: State.Search,
    vehicleUUID: UUID,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (tyreToBind, setTyreToBind) = rememberSaveable { mutableStateOf<Tyre?>(null) }
    val roundedTopId = remember(state) { state.computeRoundedTopId() }
    val roundedBottomId = remember(state) { state.computeRoundedBottomId() }
    LazyColumn(modifier.fillMaxWidth()) {

        currentVehicleItems(
            currentVehicleName = state.currentVehicleName,
            currentVehicleKind = state.currentVehicleKind,
            boundSensorToCurrentVehicle = state.boundSensorToCurrentVehicle,
            unboundTyres = when (state) {
                is State.Searching -> state.unboundTyres
                is State.Completed -> emptyList()
            },
            pressureUnit = state.pressureUnit,
            temperatureUnit = state.temperatureUnit,
            roundedTopId = roundedTopId,
            roundedBottomId = roundedBottomId,
            setTyreToBind = setTyreToBind
        )

        if (state is State.Searching)
            placeholder(
                roundedBottomId = roundedBottomId,
                roundedTopId = roundedTopId
            )

        if (state is State.Searching && state.unboundTyres.isNotEmpty())
            currentVehicleTyreFoundMessage(
                unboundTyres = state.unboundTyres
            )

        if (state is State.Searching)
            currentVehiclePlaceholderMessage()

        if (state is State.Searching && state.boundTyresToOtherVehicle.isNotEmpty())
            otherVehicleItems(
                boundTyresToOtherVehicle = state.boundTyresToOtherVehicle,
                temperatureUnit = state.temperatureUnit,
                pressureUnit = state.pressureUnit,
                setTyreToBind = setTyreToBind
            )

        if (state is State.Completed)
            allWheelsBoundMessage(
                currentVehicleName = state.currentVehicleName,
                bindingFinished = bindingFinished
            )
    }
    if (tyreToBind != null)
        BindDialog(
            vehicleUuid = vehicleUUID,
            tyre = tyreToBind,
            onBind = { setTyreToBind(null) },
            onDismissRequest = { setTyreToBind(null) },
        )
}

private fun State.Search.computeRoundedTopId() = when (this) {
    is State.Searching ->
        boundSensorToCurrentVehicle.firstOrNull()?.first?.id
            ?: unboundTyres.firstOrNull()?.sensorId
            ?: -1

    is State.Completed -> boundSensorToCurrentVehicle.firstOrNull()?.first?.id
}


private fun State.Search.computeRoundedBottomId() = when (this) {
    is State.Searching -> -1
    is State.Completed -> boundSensorToCurrentVehicle.lastOrNull()?.first?.id
}

@ExperimentalFoundationApi
private fun LazyListScope.currentVehicleItems(
    currentVehicleName: String,
    currentVehicleKind: Vehicle.Kind,
    boundSensorToCurrentVehicle: List<Pair<Sensor, Tyre.Unlocated?>>,
    unboundTyres: List<Tyre.Unlocated>,
    pressureUnit: PressureUnit,
    temperatureUnit: TemperatureUnit,
    roundedTopId: Int?,
    roundedBottomId: Int?,
    setTyreToBind: (Tyre.Unlocated) -> Unit
) {
    item {
        Text(text = "Sensors for ${currentVehicleName}:", fontSize = 12.sp)
    }
    item { Spacer(Modifier.height(8.dp)) }

    // Bound sensors
    items(
        items = boundSensorToCurrentVehicle,
        key = { (sensor) -> sensor.id }
    ) { (sensor, tyre) ->
        BoundSensorCell(
            kind = currentVehicleKind,
            sensor = sensor,
            tyre = tyre,
            roundedTop = sensor.id == roundedTopId,
            roundedBottom = sensor.id == roundedBottomId
        )
        if (sensor.id != roundedBottomId) HorizontalDivider(thickness = Hairline)
    }

    // Unbound tyres
    itemsIndexed(
        items = unboundTyres,
        key = { _, tyre -> tyre.sensorId }
    ) { index, tyre ->
        TyreCell(
            tyre = tyre,
            sensorToVehicle = null,
            temperatureUnit,
            pressureUnit,
            showClosest = index == 0 && unboundTyres.size >= 2,
            showFarthest = index.plus(1) == unboundTyres.size && unboundTyres.size >= 2,
            roundedTop = tyre.sensorId == roundedTopId,
            roundedBottom = tyre.sensorId == roundedBottomId,
            onBind = { setTyreToBind(tyre) },
            modifier = Modifier.fillParentMaxWidth()
        )
        if (tyre.sensorId != roundedBottomId) HorizontalDivider(thickness = Hairline)
    }
}

private fun LazyListScope.placeholder(
    roundedTopId: Int?,
    roundedBottomId: Int?,
) {
    item {
        PlaceholderTyreCell(
            roundedTop = -1 == roundedTopId,
            roundedBottom = -1 == roundedBottomId,
            modifier = Modifier.fillParentMaxWidth()
        )
        if (-1 != roundedBottomId) HorizontalDivider(thickness = Hairline)
    }
}

private fun LazyListScope.currentVehicleTyreFoundMessage(
    unboundTyres: List<Tyre.Unlocated>,
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (unboundTyres.size == 1) "Bind the sensor above ☝️"
            else "Bind one of the sensors above ☝️",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

private fun LazyListScope.currentVehiclePlaceholderMessage() {
    item {
        Spacer(Modifier.height(8.dp))
        Text(
            text = AnnotatedString("Plug and bind a ")
                .plus(
                    AnnotatedString(
                        "single sensor",
                        SpanStyle(textDecoration = TextDecoration.Underline)
                    )
                )
                .plus(AnnotatedString(" at time")),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillParentMaxWidth()
        )
        Text(
            text = "If the sensor stays invisible, remove it from the wheel and plug in again",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

private fun LazyListScope.otherVehicleItems(
    boundTyresToOtherVehicle: List<Triple<Vehicle, Sensor, Tyre.Unlocated>>,
    temperatureUnit: TemperatureUnit,
    pressureUnit: PressureUnit,
    setTyreToBind: (Tyre.Unlocated) -> Unit
) {
    item { Spacer(modifier = Modifier.height(56.dp)) }
    item {
        Text(
            text = "Already bound sensors found:",
            fontSize = 12.sp,
        )
    }
    item { Spacer(Modifier.height(8.dp)) }
    itemsIndexed(
        items = boundTyresToOtherVehicle,
        key = { _, (_, _, tyre) -> tyre.sensorId }
    ) { index, (vehicle, sensor, tyre) ->
        TyreCell(
            tyre = tyre,
            sensorToVehicle = sensor to vehicle,
            temperatureUnit = temperatureUnit,
            pressureUnit = pressureUnit,
            showClosest = index == 0 && boundTyresToOtherVehicle.size >= 2,
            showFarthest = index.plus(1) == boundTyresToOtherVehicle.size && boundTyresToOtherVehicle.size >= 2,
            roundedTop = index == 0,
            roundedBottom = index == boundTyresToOtherVehicle.size.minus(1),
            onBind = { setTyreToBind(tyre) },
            modifier = Modifier.fillParentMaxWidth()
        )
        if (index.plus(1) < boundTyresToOtherVehicle.size) HorizontalDivider(thickness = Hairline)
    }
    item { Spacer(Modifier.height(4.dp)) }
}

private fun LazyListScope.allWheelsBoundMessage(
    currentVehicleName: String,
    bindingFinished: () -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Every tyre of your vehicle \"${currentVehicleName}\" is bound to a sensor",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillParentMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            Modifier
                .fillParentMaxWidth()
                .testTag(bindingFinishedGoBackButton)
        ) {
            Button(
                onClick = bindingFinished,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Go back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TyreCell(
    tyre: Tyre,
    sensorToVehicle: Pair<Sensor, Vehicle>?,
    temperatureUnit: TemperatureUnit,
    pressureUnit: PressureUnit,
    showClosest: Boolean,
    showFarthest: Boolean,
    roundedTop: Boolean,
    roundedBottom: Boolean,
    onBind: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false,
) {
    val locale = LocalConfiguration.current.locales[0]
    val df = remember(locale) { DateFormat.getTimeInstance(SHORT, locale) }
    ElevatedCard(
        shape = roundedShape(roundedTop, roundedBottom),
        enabled = isPlaceholder.not(),
        onClick = onBind,
        modifier = modifier.testTag(UnlocatedSensorListTags.tyreCell(tyre.sensorId)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .padding(start = 8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Found tyre at ${df.format(Date(tyre.timestamp.seconds.inWholeMilliseconds))}",
                    fontSize = 14.sp,
                    modifier = Modifier.placeholder(
                        visible = isPlaceholder,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
                )
                Text(
                    text = if (sensorToVehicle != null)
                        StringBuilder()
                            .append("Bound to the ")
                            .appendLoc(sensorToVehicle.first.location)
                            .append(" of ")
                            .append(sensorToVehicle.second.name)
                            .toString()
                    else
                        "${tyre.pressure.string(pressureUnit)} / " +
                                tyre.temperature.string(temperatureUnit),
                    fontSize = 11.sp,
                    modifier = Modifier.placeholder(
                        visible = isPlaceholder,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
                )
            }
            when {
                showClosest -> Text("Closest", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                showFarthest -> Text("Farthest", fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.link_variant_plus),
                contentDescription = null,
                modifier = Modifier
                    .minimumInteractiveComponentSize() // To make this icon look like an IconButton
                    .placeholder(
                        visible = isPlaceholder,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
        }
    }
}

@Composable
private fun PlaceholderTyreCell(
    modifier: Modifier = Modifier,
    tyre: Tyre = Tyre.Unlocated(ts, 0, Int.MAX_VALUE, 2f.bar, 20f.celsius, 50u, false),
    temperatureUnit: TemperatureUnit = CELSIUS,
    pressureUnit: PressureUnit = BAR,
    showClosest: Boolean = false,
    showFarthest: Boolean = false,
    roundedTop: Boolean = true,
    roundedBottom: Boolean = true,
    onBind: () -> Unit = { },
) {
    TyreCell(
        tyre = tyre,
        sensorToVehicle = null,
        temperatureUnit = temperatureUnit,
        pressureUnit = pressureUnit,
        showClosest = showClosest,
        showFarthest = showFarthest,
        roundedTop = roundedTop,
        roundedBottom = roundedBottom,
        onBind = onBind,
        isPlaceholder = true,
        modifier = modifier
    )
}

@Composable
private fun BoundSensorCell(
    kind: Vehicle.Kind,
    sensor: Sensor,
    tyre: Tyre.Unlocated?,
    roundedTop: Boolean,
    roundedBottom: Boolean,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales.get(0)
    val df = remember(locale) { DateFormat.getTimeInstance(SHORT, locale) }
    ElevatedCard(
        shape = roundedShape(roundedTop, roundedBottom),
        modifier = modifier.testTag(UnlocatedSensorListTags.boundCell(sensor.id)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .padding(start = 8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = StringBuilder()
                        .appendLoc(sensor.location)
                        .append(" is bound")
                        .toString()
                        .capitalize(LocaleList.current),
                    fontSize = 14.sp,
                )
                if (tyre != null)
                    Text(
                        text = "Sensor seen at ${df.format(Date(tyre.timestamp.seconds.inWholeMilliseconds))}",
                        fontSize = 11.sp,
                    )
            }

            val states = remember(sensor.location) {
                kind.locations
                    .associateWith {
                        if (it == sensor.location) WheelState.Fade
                        else WheelState.Empty
                    }
                    .toImmutableMap()
            }
            Vehicle(
                kind = kind,
                states = states,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .minimumInteractiveComponentSize()
            )
        }
    }
}

private fun roundedShape(
    roundedTop: Boolean,
    roundedBottom: Boolean,
) = when {
    roundedTop && roundedBottom -> ShapeDefaults.Medium

    roundedTop -> ShapeDefaults.Medium.copy(
        bottomStart = ZeroCornerSize,
        bottomEnd = ZeroCornerSize
    )

    roundedBottom -> ShapeDefaults.Medium.copy(
        topStart = ZeroCornerSize,
        topEnd = ZeroCornerSize
    )

    else -> RectangleShape
}

@Composable
private fun Issue(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.alert_octagon),
                contentDescription = "There is an issue to find the sensor",
                tint = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Failed to search for sensors, there is an issue with the Bluetooth chip",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent().apply {
                        action = Settings.ACTION_BLUETOOTH_SETTINGS
                    })
                },
            ) {
                Text("Disable Bluetooth")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = context::restartApp) {
                Text("Restart the app")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun AllWheelsAreAlreadyBoundPreview() {
    Content(
        UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(State.AllWheelsAreAlreadyBound(Vehicle.Kind.CAR))
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun UnplugEverySensorPreview() {
    Content(
        UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(State.UnplugEverySensor)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun SearchingNoResultPreview() {
    Content(
        UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                "MOCK",
                Vehicle.Kind.CAR,
                emptyList(),
                emptyList(),
                emptyList(),
                BAR,
                CELSIUS,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun SearchingFoundSingleTyrePreview() {
    Content(
        vehicleUuid = UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                "MOCK",
                Vehicle.Kind.CAR,
                emptyList(),
                listOf(Tyre.Unlocated(ts, -20, 1, 1.5f.bar, 20f.celsius, 20u, false)),
                emptyList(),
                BAR,
                CELSIUS,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun SearchingFoundMultipleTyrePreview() {
    Content(
        vehicleUuid = UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                "MOCK",
                Vehicle.Kind.CAR,
                listOf(
                    Pair(
                        Sensor(0, Vehicle.Kind.Location.Wheel(FRONT_LEFT)),
                        Tyre.Unlocated(ts, -20, 0, 1.5f.bar, 20f.celsius, 20u, false),
                    )
                ),
                listOf(
                    Tyre.Unlocated(ts, -20, 1, 1.5f.bar, 20f.celsius, 20u, false),
                    Tyre.Unlocated(ts, -20, 2, 1.75f.bar, 17f.celsius, 20u, false),
                    Tyre.Unlocated(ts, -20, 3, 2f.bar, 18f.celsius, 20u, false),
                ),
                listOf(
                    Triple(
                        mockVehicle(),
                        mockSensor(4),
                        Tyre.Unlocated(ts, -20, 4, 1.5f.bar, 20f.celsius, 20u, false),
                    ),
                    Triple(
                        mockVehicle(),
                        mockSensor(5),
                        Tyre.Unlocated(ts, -20, 5, 1.75f.bar, 17f.celsius, 20u, false),
                    ),
                    Triple(
                        mockVehicle(),
                        mockSensor(6),
                        Tyre.Unlocated(ts, -20, 6, 2f.bar, 18f.celsius, 20u, false),
                    ),
                ),
                BAR,
                CELSIUS,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun SearchingFoundOnlyBoundTyrePreview() {
    Content(
        vehicleUuid = UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                "MOCK",
                Vehicle.Kind.CAR,
                listOf(Sensor(0, Vehicle.Kind.Location.Wheel(FRONT_LEFT)) to null),
                emptyList(),
                listOf(
                    Triple(
                        mockVehicle(),
                        mockSensor(4),
                        Tyre.Unlocated(ts, -20, 4, 1.5f.bar, 20f.celsius, 20u, false),
                    ),
                    Triple(
                        mockVehicle(),
                        mockSensor(5),
                        Tyre.Unlocated(ts, -20, 5, 1.75f.bar, 17f.celsius, 20u, false),
                    ),
                    Triple(
                        mockVehicle(),
                        mockSensor(6),
                        Tyre.Unlocated(ts, -20, 6, 2f.bar, 18f.celsius, 20u, false),
                    ),
                ),
                BAR,
                CELSIUS,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun CompletedPreview() {
    Content(
        vehicleUuid = UUID.randomUUID(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Completed(
                "MOCK",
                Vehicle.Kind.CAR,
                listOf(
                    Sensor(0, Vehicle.Kind.Location.Wheel(FRONT_LEFT)) to null,
                    Pair(
                        Sensor(1, Vehicle.Kind.Location.Wheel(FRONT_RIGHT)),
                        Tyre.Unlocated(ts, -20, 1, 1.5f.bar, 20f.celsius, 20u, false),
                    ),
                    Sensor(2, Vehicle.Kind.Location.Wheel(REAR_LEFT)) to null,
                    Sensor(3, Vehicle.Kind.Location.Wheel(REAR_RIGHT)) to null
                ),
                BAR,
                CELSIUS,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
internal fun IssuePreview() {
    Content(
        vehicleUuid = UUID.randomUUID(),
        bindingFinished = { },
        viewModel = MockListSensorViewModel(State.Issue)
    )
}

private class MockListSensorViewModel(state: State) : ListSensorViewModel {
    override val stateFlow = MutableStateFlow(state)
    override fun acknowledgeAndClearBinding() = error("")
    override fun acknowledgeSensorUnplugged() = error("")
}

@Suppress("ConstPropertyName")
internal object UnlocatedSensorListTags {
    const val root = "UnlocatedSensorListTags_root"
    const val clearBindingsAndContinueButton =
        "UnlocatedSensorListTags_clearBindingsAndContinueButton"
    const val sensorUnpluggedButton = "UnlocatedSensorListTags_sensorUnpluggedButton"
    fun tyreCell(sensorId: Int) = "UnlocatedSensorListTags_tyreCell_$sensorId"
    fun boundCell(sensorId: Int) = "UnlocatedSensorListTags_boundCell_$sensorId"
    const val bindingFinishedGoBackButton = "UnlocatedSensorListTags_bindingFinishedGoBackButton"
}
