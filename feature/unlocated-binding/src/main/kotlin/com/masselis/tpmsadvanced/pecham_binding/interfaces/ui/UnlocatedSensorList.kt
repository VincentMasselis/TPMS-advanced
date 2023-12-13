package com.masselis.tpmsadvanced.pecham_binding.interfaces.ui

import android.icu.text.DateFormat.SHORT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.ListSensorViewModel
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.ioc.FeatureUnlocatedBinding.Companion.ListSensorViewModel
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
    vehicle: Vehicle,
    modifier: Modifier = Modifier
) {
    Content(
        vehicle = vehicle,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
    viewModel: ListSensorViewModel = viewModel(key = "ListSensorViewModel_${vehicle.kind}") {
        ListSensorViewModel(vehicle.kind)
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
                bound = null,
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
            itemsIndexed(
                items = state.listBoundTyre,
                key = { _, it -> it.tyre.id }
            ) { index, it ->
                TyreCell(
                    tyre = it.tyre,
                    state.temperatureUnit,
                    state.pressureUnit,
                    showClosest = index == 0 && state.listBoundTyre.size >= 2,
                    showFarthest = index.plus(1) == state.listBoundTyre.size && state.listBoundTyre.size >= 2,
                    bound = it,
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
            vehicle = vehicle,
            tyre = tyreToBind.tyre,
            onDismissRequest = { setTyreToBind(null) },
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
    bound: Available.Bound?,
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
                text = StringBuilder()
                    .run {
                        append(
                            if (bound != null) "Sensor bounded to \"${bound.vehicle.name}\" found at "
                            else "Found tyre at "
                        )
                    }
                    .append(df.format(Date(tyre.timestamp.seconds.inWholeMilliseconds))).toString(),
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

private val df = DateFormat.getTimeInstance(SHORT)

@Composable
private fun PlaceholderTyreCell(
    modifier: Modifier = Modifier
) {
    TyreCell(
        tyre = Tyre.Unlocated(now(), 0, Int.MAX_VALUE, 2f.bar, 20f.celsius, 50u, false),
        temperatureUnit = CELSIUS,
        pressureUnit = BAR,
        showClosest = false,
        showFarthest = false,
        bound = null,
        onBind = { },
        isPlaceholder = true,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun ContentEmpty() {
    Content(mockVehicle(), viewModel = MockListSensorViewModel(State.Empty))
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun ContentSensors() {
    Content(
        vehicle = mockVehicle(),
        viewModel = MockListSensorViewModel(
            State.Tyres(
                Vehicle.Kind.CAR, listOf(
                    Available.ReadyToBind(
                        Tyre.Unlocated(now(), -20, 1, 1.5f.bar, 20f.celsius, 20u, false)
                    ),
                    Available.ReadyToBind(
                        Tyre.Unlocated(now(), -20, 2, 1.75f.bar, 17f.celsius, 20u, false)
                    ),
                    Available.ReadyToBind(
                        Tyre.Unlocated(now(), -20, 3, 2f.bar, 18f.celsius, 20u, false)
                    ),
                ), listOf(
                    Available.Bound(
                        Tyre.Unlocated(now(), -20, 4, 1.5f.bar, 20f.celsius, 20u, false),
                        mockSensor(4),
                        mockVehicle()
                    ),
                    Available.Bound(
                        Tyre.Unlocated(now(), -20, 5, 1.75f.bar, 17f.celsius, 20u, false),
                        mockSensor(5),
                        mockVehicle()
                    ),
                    Available.Bound(
                        Tyre.Unlocated(now(), -20, 6, 2f.bar, 18f.celsius, 20u, false),
                        mockSensor(6),
                        mockVehicle()
                    ),
                ), BAR, CELSIUS
            )
        )
    )
}

private class MockListSensorViewModel(state: State) : ListSensorViewModel {
    override val stateFlow = MutableStateFlow(state)
}