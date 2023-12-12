package com.masselis.tpmsadvanced.pecham_binding.interfaces

import android.icu.text.DateFormat.SHORT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.pecham_binding.interfaces.ListSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.ioc.FeatureCoreUnlocatedBinding.Companion.ListSensorViewModel
import com.masselis.tpmsadvanced.pecham_binding.usecase.ListTyreUseCase.Available
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Composable
public fun UnlocatedSensorList(
    vehicleUuid: UUID, vehicleKind: Vehicle.Kind, modifier: Modifier = Modifier
) {
    Content(
        vehicleUuid, vehicleKind, modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    vehicleUuid: UUID,
    vehicleKind: Vehicle.Kind,
    modifier: Modifier = Modifier,
    viewModel: ListSensorViewModel = viewModel(key = "ListSensorViewModel_${vehicleUuid}_$vehicleKind") {
        ListSensorViewModel(vehicleUuid, vehicleKind)
    }
) {
    val state = viewModel.stateFlow.collectAsState().value
    val (tyreToBind, setTyreToBind) = remember { mutableStateOf<Available?>(null) }
    LazyColumn(modifier) {
        stickyHeader {
            Text(
                text = "Sensors ready to bind:",
                fontSize = 12.sp,
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        if (state is State.Empty) item {
            PlaceholderTyreCell(
                Modifier
                    .fillMaxWidth()
                    .tyre(0, 1)
            )
        }
        if (state is State.Tyres && state.listReadyToBind.isNotEmpty()) itemsIndexed(items = state.listReadyToBind,
            key = { _, it -> it.tyre.id }) { index, it ->
            TyreCell(
                tyre = it.tyre,
                state.temperatureUnit,
                state.pressureUnit,
                showClosest = index == 0 && state.listReadyToBind.size >= 2,
                showFarthest = index.plus(1) == state.listReadyToBind.size && state.listReadyToBind.size >= 2,
                isReadyToBind = true,
                onBind = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .tyre(index, state.listReadyToBind.size)
            )
            if (index.plus(1) < state.listReadyToBind.size) Divider(thickness = Hairline)
        }
        if (state is State.Issue) item {
            TODO()
        } else item {
            Box(
                Modifier.fillParentMaxWidth(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(Modifier.padding(top = 8.dp))
            }
        }
        if (state is State.Tyres && state.listReadyToBind.isNotEmpty() && state.listBoundTyre.isNotEmpty()) item {
            Spacer(modifier = Modifier.height(25.dp))
        }
        if (state is State.Tyres && state.listBoundTyre.isNotEmpty()) {
            stickyHeader {
                Text(
                    text = "Sensors already bound:",
                    fontSize = 12.sp,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            itemsIndexed(items = state.listBoundTyre, key = { _, it -> it.tyre.id }) { index, it ->
                TyreCell(
                    tyre = it.tyre,
                    state.temperatureUnit,
                    state.pressureUnit,
                    showClosest = index == 0 && state.listBoundTyre.size >= 2,
                    showFarthest = index.plus(1) == state.listBoundTyre.size && state.listBoundTyre.size >= 2,
                    isReadyToBind = false,
                    onBind = { setTyreToBind(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .tyre(index, state.listBoundTyre.size)
                )
                if (index.plus(1) < state.listBoundTyre.size) Divider(thickness = Hairline)
            }
        }
    }
    if (tyreToBind != null)
        BindDialog(
            available = tyreToBind,
            vehicleKind = vehicleKind,
            onDismissRequest = { setTyreToBind(null) },
            onBind = {
                viewModel.bind(it, tyreToBind.tyre)
                setTyreToBind(null)
            }
        )
}

private fun Modifier.tyre(index: Int, listSize: Int) = this
    .run {
        when {
            index == 0 && listSize == 1 -> clip(
                RoundedCornerShape(10.dp)
            )

            index == 0 -> clip(
                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )

            index.plus(1) == listSize -> clip(
                RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
            )

            else -> this
        }
    }
    .background(Color.White)
    .padding(start = 8.dp)

@Composable
private fun TyreCell(
    tyre: Tyre,
    temperatureUnit: TemperatureUnit,
    pressureUnit: PressureUnit,
    showClosest: Boolean,
    showFarthest: Boolean,
    isReadyToBind: Boolean,
    onBind: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = StringBuilder().run {
                    append(
                        if (isReadyToBind) "Found tyre at "
                        else "Bound tyre found at "
                    )
                }.append(df.format(Date(tyre.timestamp.seconds.inWholeMilliseconds))).toString(),
                fontSize = 14.sp,
                modifier = Modifier.placeholder(
                    visible = isPlaceholder,
                    highlight = PlaceholderHighlight.shimmer(),
                )
            )
            Text(
                text = "${tyre.pressure.string(pressureUnit)} / ${
                    tyre.temperature.string(
                        temperatureUnit
                    )
                }", fontSize = 11.sp, modifier = Modifier.placeholder(
                    visible = isPlaceholder,
                    highlight = PlaceholderHighlight.shimmer(),
                )
            )
        }
        when {
            showClosest -> Text("Closest", fontSize = 12.sp, fontStyle = FontStyle.Italic)
            showFarthest -> Text("Farthest", fontSize = 12.sp, fontStyle = FontStyle.Italic)
        }

        IconButton(onClick = onBind) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isReadyToBind) R.drawable.link_variant_plus
                    else R.drawable.link_variant_remove
                ), contentDescription = null, modifier = Modifier.placeholder(
                    visible = isPlaceholder,
                    highlight = PlaceholderHighlight.shimmer(),
                )
            )
        }
    }
}

private val df = DateFormat.getTimeInstance(SHORT)

@Composable
private fun PlaceholderTyreCell(
    modifier: Modifier = Modifier
) {
    TyreCell(
        tyre = Tyre(now(), 0, null, Int.MAX_VALUE, 2f.bar, 20f.celsius, 50u, false),
        temperatureUnit = CELSIUS,
        pressureUnit = BAR,
        showClosest = false,
        showFarthest = false,
        isReadyToBind = true,
        onBind = { },
        isPlaceholder = true,
        modifier = modifier
    )
}

@Composable
private fun BindDialog(
    available: Available,
    vehicleKind: Vehicle.Kind,
    onDismissRequest: () -> Unit,
    onBind: (Vehicle.Kind.Location) -> Unit
) {
    var selectedLocation by remember { mutableStateOf<Vehicle.Kind.Location?>(null) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Ready to bind") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (available is Available.Bound) {
                    Text(
                        text = "⚠️ This sensor is already bound to the vehicle " +
                                "\"${available.vehicle.name}\" and attached to the " +
                                available.vehicle
                                    .kind
                                    .computeLocations(setOf(available.sensor.location))
                                    .first()
                                    .let {
                                        when (it) {
                                            is Vehicle.Kind.Location.Axle -> when (it.axle) {
                                                FRONT -> "front"
                                                REAR -> "rear"
                                            }

                                            is Vehicle.Kind.Location.Side -> when (it.side) {
                                                LEFT -> "left"
                                                RIGHT -> "right"
                                            }

                                            is Vehicle.Kind.Location.Wheel -> when (it.location) {
                                                FRONT_LEFT -> "front left"
                                                FRONT_RIGHT -> "front right"
                                                REAR_LEFT -> "rear left"
                                                REAR_RIGHT -> "rear right"
                                            }
                                        }
                                    } +
                                " wheel"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text =
                    if (available is Available.Bound) "Tap a wheel to replace the binding:"
                    else "Tap the wheel to bind:",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (vehicleKind) {
                    Vehicle.Kind.CAR -> Car(
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.MOTORCYCLE -> Motorcycle(
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.TADPOLE_THREE_WHEELER -> TadpoleThreeWheeler(
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedLocation != null,
                onClick = { onBind(selectedLocation!!) }
            ) {
                Text(text = "Bind")
            }
        }
    )
}

@Composable
private fun Car(
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_LEFT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_RIGHT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun SingleAxleTrailer(
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Side(LEFT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        with(Vehicle.Kind.Location.Side(RIGHT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun Motorcycle(
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Axle(FRONT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Vehicle.Kind.Location.Axle(REAR)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TadpoleThreeWheeler(
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Vehicle.Kind.Location.Axle(REAR)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun DeltaThreeWheeler(
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Axle(FRONT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_LEFT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_RIGHT)) {
            Tyre(
                isSelected = selectedLocation == this,
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun Tyre(
    isSelected: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(RoundedCornerShape(percent = 20))
            .height(35.dp)
            .aspectRatio(15f / 40f)
            .run {
                if (isSelected)
                    background(MaterialTheme.colorScheme.primary)
                else
                    background(Color.Transparent)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(percent = 20)
                        )
            }
            .clickable(onClick = onTap)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun ContentEmpty() {
    Content(
        UUID.randomUUID(), Vehicle.Kind.CAR, viewModel = MockListSensorViewModel(State.Empty)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun ContentSensors() {
    Content(
        UUID.randomUUID(), Vehicle.Kind.CAR, viewModel = MockListSensorViewModel(
            State.Tyres(
                Vehicle.Kind.CAR, listOf(
                    Available.ReadyToBind(
                        Tyre(now(), -20, null, 1, 1.5f.bar, 20f.celsius, 20u, false)
                    ),
                    Available.ReadyToBind(
                        Tyre(now(), -20, null, 2, 1.75f.bar, 17f.celsius, 20u, false)
                    ),
                    Available.ReadyToBind(
                        Tyre(now(), -20, null, 3, 2f.bar, 18f.celsius, 20u, false)
                    ),
                ), listOf(
                    Available.Bound(
                        Tyre(now(), -20, null, 4, 1.5f.bar, 20f.celsius, 20u, false),
                        mockSensor(4),
                        mockVehicle()
                    ),
                    Available.Bound(
                        Tyre(now(), -20, null, 5, 1.75f.bar, 17f.celsius, 20u, false),
                        mockSensor(5),
                        mockVehicle()
                    ),
                    Available.Bound(
                        Tyre(now(), -20, null, 6, 2f.bar, 18f.celsius, 20u, false),
                        mockSensor(6),
                        mockVehicle()
                    ),
                ), BAR, CELSIUS
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogPreview() {
    BindDialog(
        available = Available.ReadyToBind(mockTyre(1)),
        vehicleKind = Vehicle.Kind.CAR,
        onDismissRequest = {},
        onBind = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTrailerPreview() {
    BindDialog(
        available = Available.ReadyToBind(mockTyre(1)),
        vehicleKind = Vehicle.Kind.SINGLE_AXLE_TRAILER,
        onDismissRequest = {},
        onBind = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogMotorcyclePreview() {
    BindDialog(
        available = Available.ReadyToBind(mockTyre(1)),
        vehicleKind = Vehicle.Kind.MOTORCYCLE,
        onDismissRequest = {},
        onBind = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTadpolePreview() {
    BindDialog(
        available = Available.ReadyToBind(mockTyre(1)),
        vehicleKind = Vehicle.Kind.TADPOLE_THREE_WHEELER,
        onDismissRequest = {},
        onBind = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogDeltaPreview() {
    BindDialog(
        available = Available.ReadyToBind(mockTyre(1)),
        vehicleKind = Vehicle.Kind.DELTA_THREE_WHEELER,
        onDismissRequest = {},
        onBind = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogAlreadyBoundPreview() {
    BindDialog(
        available = Available.Bound(
            mockTyre(1),
            mockSensor(1),
            mockVehicle(kind = Vehicle.Kind.MOTORCYCLE)
        ),
        vehicleKind = Vehicle.Kind.CAR,
        onDismissRequest = {},
        onBind = {}
    )
}

private fun mockTyre(
    id: Int,
    timestamp: Double = now(),
    rssi: Int = -20,
    location: SensorLocation? = null,
    pressure: Pressure = 2f.bar,
    temperature: Temperature = 20f.celsius,
    battery: UShort = 50u,
    isAlarm: Boolean = false
) = Tyre(timestamp, rssi, location, id, pressure, temperature, battery, isAlarm)

private fun mockSensor(
    id: Int, location: SensorLocation = FRONT_LEFT
) = Sensor.Located(id, location)

private fun mockVehicle(
    uuid: UUID = UUID.randomUUID(),
    kind: Vehicle.Kind = Vehicle.Kind.CAR,
    name: String = "MOCK",
    lowPressure: Pressure = 1f.bar,
    highPressure: Pressure = 5f.bar,
    lowTemp: Temperature = 15f.celsius,
    normalTemp: Temperature = 25f.celsius,
    highTemp: Temperature = 45f.celsius,
    isBackgroundMonitor: Boolean = false,
) = Vehicle(
    uuid, kind, name, lowPressure, highPressure, lowTemp, normalTemp, highTemp, isBackgroundMonitor
)

private class MockListSensorViewModel(state: State) : ListSensorViewModel {
    override val stateFlow = MutableStateFlow(state)
    override fun bind(location: Vehicle.Kind.Location, tyre: Tyre) = error("")
}