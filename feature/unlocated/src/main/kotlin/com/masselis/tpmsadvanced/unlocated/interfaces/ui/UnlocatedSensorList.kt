package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import android.icu.text.DateFormat.SHORT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModel
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.ioc.FeatureUnlocatedBinding.Companion.ListSensorViewModel
import com.masselis.tpmsadvanced.unlocated.usecase.ListTyreUseCase.Available
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DateFormat
import java.util.Date
import kotlin.time.Duration.Companion.seconds

@Composable
public fun UnlocatedSensorList(
    vehicle: Vehicle,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Content(
        vehicle = vehicle,
        bindingFinished = bindingFinished,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    vehicle: Vehicle,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListSensorViewModel = viewModel { ListSensorViewModel(vehicle) }
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(end = 16.dp, start = 16.dp)
    ) {
        when (val state = viewModel.stateFlow.collectAsState().value) {
            State.UnplugEverySensor -> UnplugEverySensor(
                onAcknowledge = viewModel::acknowledgeSensorUnplugged,
            )

            is State.Searching -> Searching(
                state = state,
                vehicle = vehicle,
                onSensorBound = viewModel::onSensorBound,
                bindingFinished = bindingFinished,
            )

            State.Issue -> TODO()
        }
    }
}

@Composable
private fun UnplugEverySensor(
    onAcknowledge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text("Before we start, ensure to unplug every sensor to bind from your tyres")
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = onAcknowledge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sensors unplugged")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Searching(
    state: State.Searching,
    vehicle: Vehicle,
    onSensorBound: () -> Unit,
    bindingFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (tyreToBind, setTyreToBind) = rememberSaveable { mutableStateOf<Available?>(null) }
    LazyColumn(modifier.fillMaxWidth()) {
        stickyHeader {
            Text(text = "Sensors ready to bind:", fontSize = 12.sp)
        }
        item { Spacer(Modifier.height(8.dp)) }
        when (val readyToBind = state.bindListState) {
            State.Searching.ShowPlaceholder ->
                item {
                    PlaceholderTyreCell(modifier = Modifier.fillParentMaxWidth())
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
                }

            is State.Searching.ShowList -> {
                itemsIndexed(
                    items = readyToBind.listReadyToBind,
                    key = { _, it -> it.tyre.id }) { index, it ->
                    TyreCell(
                        tyre = it.tyre,
                        state.temperatureUnit,
                        state.pressureUnit,
                        showClosest = index == 0 && readyToBind.listReadyToBind.size >= 2,
                        showFarthest = index.plus(1) == readyToBind.listReadyToBind.size && readyToBind.listReadyToBind.size >= 2,
                        alreadyBound = null,
                        roundedTop = index == 0,
                        roundedBottom = index == readyToBind.listReadyToBind.size.minus(1),
                        onBind = { setTyreToBind(it) },
                        modifier = Modifier.fillParentMaxWidth()
                    )
                    if (index.plus(1) < readyToBind.listReadyToBind.size) Divider(thickness = Hairline)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (readyToBind.listReadyToBind.size == 1) "Bind the sensor above ☝️"
                        else "Bind one of the sensors above ☝️",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillParentMaxWidth()
                    )
                }
            }
        }
        if (state.listAlreadyBoundTyre.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(25.dp)) }
            stickyHeader {
                Text(
                    text = "Sensors already bound:",
                    fontSize = 12.sp,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            itemsIndexed(
                items = state.listAlreadyBoundTyre,
                key = { _, it -> it.tyre.id }
            ) { index, it ->
                TyreCell(
                    tyre = it.tyre,
                    state.temperatureUnit,
                    state.pressureUnit,
                    showClosest = index == 0 && state.listAlreadyBoundTyre.size >= 2,
                    showFarthest = index.plus(1) == state.listAlreadyBoundTyre.size && state.listAlreadyBoundTyre.size >= 2,
                    alreadyBound = it,
                    roundedTop = index == 0,
                    roundedBottom = index == state.listAlreadyBoundTyre.size.minus(1),
                    onBind = { setTyreToBind(it) },
                    modifier = Modifier.fillParentMaxWidth()
                )
                if (index.plus(1) < state.listAlreadyBoundTyre.size) Divider(thickness = Hairline)
            }
            item { Spacer(Modifier.height(4.dp)) }
        }
        if (state.allWheelsBound) item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Every tyre of your vehicle \"${vehicle.name}\" is bound to a sensor",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillParentMaxWidth()
            )
            Box(Modifier.fillParentMaxWidth()) {
                Button(
                    onClick = bindingFinished,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Go back")
                }
            }
        }
    }
    if (tyreToBind != null)
        BindDialog(
            vehicleUuid = vehicle.uuid,
            tyre = tyreToBind.tyre,
            onBound = {
                onSensorBound()
                setTyreToBind(null)
            },
            onDismissRequest = { setTyreToBind(null) },
        )
}

@Composable
private fun TyreCell(
    tyre: Tyre,
    temperatureUnit: TemperatureUnit,
    pressureUnit: PressureUnit,
    showClosest: Boolean,
    showFarthest: Boolean,
    roundedTop: Boolean,
    roundedBottom: Boolean,
    alreadyBound: Available.AlreadyBound?,
    onBind: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false,
) {
    Surface(
        shape = when {
            roundedTop && roundedBottom -> RoundedCornerShape(12.dp)
            roundedTop -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            roundedBottom -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            else -> RectangleShape
        },
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
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
                        .run {
                            append(
                                if (alreadyBound != null) "Sensor bounded to \"${alreadyBound.vehicle.name}\" found at "
                                else "Found tyre at "
                            )
                        }
                        .append(df.format(Date(tyre.timestamp.seconds.inWholeMilliseconds)))
                        .toString(),
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

            IconButton(
                enabled = isPlaceholder.not(),
                onClick = onBind,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.link_variant_plus),
                    contentDescription = null,
                    modifier = Modifier
                        .placeholder(
                            visible = isPlaceholder,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                )
            }
        }
    }
}

private val df = DateFormat.getTimeInstance(SHORT)

@Composable
private fun PlaceholderTyreCell(
    modifier: Modifier = Modifier,
    tyre: Tyre = Tyre.Unlocated(now(), 0, Int.MAX_VALUE, 2f.bar, 20f.celsius, 50u, false),
    temperatureUnit: TemperatureUnit = CELSIUS,
    pressureUnit: PressureUnit = BAR,
    showClosest: Boolean = false,
    showFarthest: Boolean = false,
    roundedTop: Boolean = true,
    roundedBottom: Boolean = true,
    alreadyBound: Available.AlreadyBound? = null,
    onBind: () -> Unit = { },
) {
    TyreCell(
        tyre = tyre,
        temperatureUnit = temperatureUnit,
        pressureUnit = pressureUnit,
        showClosest = showClosest,
        showFarthest = showFarthest,
        roundedTop = roundedTop,
        roundedBottom = roundedBottom,
        alreadyBound = alreadyBound,
        onBind = onBind,
        isPlaceholder = true,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun PlugSensorFirstTry() {
    Content(
        mockVehicle(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(State.UnplugEverySensor)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun SearchingNoResultPreview() {
    Content(
        mockVehicle(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                State.Searching.ShowPlaceholder,
                emptyList(),
                BAR,
                CELSIUS,
                false
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun SearchingFoundSingleTyrePreview() {
    Content(
        vehicle = mockVehicle(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                State.Searching.ShowList(
                    listOf(
                        Available.ReadyToBind(
                            Tyre.Unlocated(now(), -20, 1, 1.5f.bar, 20f.celsius, 20u, false)
                        ),
                    )
                ),
                emptyList(),
                BAR,
                CELSIUS,
                false,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun SearchingFoundMultipleTyrePreview() {
    Content(
        vehicle = mockVehicle(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                State.Searching.ShowList(
                    listOf(
                        Available.ReadyToBind(
                            Tyre.Unlocated(now(), -20, 1, 1.5f.bar, 20f.celsius, 20u, false)
                        ),
                        Available.ReadyToBind(
                            Tyre.Unlocated(now(), -20, 2, 1.75f.bar, 17f.celsius, 20u, false)
                        ),
                        Available.ReadyToBind(
                            Tyre.Unlocated(now(), -20, 3, 2f.bar, 18f.celsius, 20u, false)
                        ),
                    )
                ),
                listOf(
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 4, 1.5f.bar, 20f.celsius, 20u, false),
                        mockSensor(4),
                        mockVehicle()
                    ),
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 5, 1.75f.bar, 17f.celsius, 20u, false),
                        mockSensor(5),
                        mockVehicle()
                    ),
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 6, 2f.bar, 18f.celsius, 20u, false),
                        mockSensor(6),
                        mockVehicle()
                    ),
                ),
                BAR,
                CELSIUS,
                true,
            )
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun SearchingFoundOnlyBoundTyrePreview() {
    Content(
        vehicle = mockVehicle(),
        bindingFinished = {},
        viewModel = MockListSensorViewModel(
            State.Searching(
                State.Searching.ShowPlaceholder,
                listOf(
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 4, 1.5f.bar, 20f.celsius, 20u, false),
                        mockSensor(4),
                        mockVehicle()
                    ),
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 5, 1.75f.bar, 17f.celsius, 20u, false),
                        mockSensor(5),
                        mockVehicle()
                    ),
                    Available.AlreadyBound(
                        Tyre.Unlocated(now(), -20, 6, 2f.bar, 18f.celsius, 20u, false),
                        mockSensor(6),
                        mockVehicle()
                    ),
                ),
                BAR,
                CELSIUS,
                false,
            )
        )
    )
}

private class MockListSensorViewModel(state: State) : ListSensorViewModel {
    override val stateFlow = MutableStateFlow(state)
    override fun acknowledgeSensorUnplugged() = error("")
    override fun onSensorBound() = error("")
}