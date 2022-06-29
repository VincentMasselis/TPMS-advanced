package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.AlertViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    viewModel: AlertViewModel = savedStateViewModel { mainComponent.alertViewModel.build(it) }
) {
    val highTempFlow = viewModel.highTemp
    val normalTempFlow = viewModel.normalTemp
    val lowTempFlow = viewModel.lowTemp
    LazyColumn(modifier = modifier.padding(end = 16.dp, start = 16.dp)) {
        item {
            LowPressureSlider(viewModel.lowPressure)
        }
        item {
            Column {
                val normalTemp by normalTempFlow.collectAsState()
                TemperatureSlider(
                    "Max temperature:",
                    mutableStateFlow = highTempFlow,
                    valueRange = normalTemp.celsius..150f
                )
            }
        }
        item {
            Column {
                val lowTemp by lowTempFlow.collectAsState()
                val highTemp by highTempFlow.collectAsState()
                TemperatureSlider(
                    "Normal temperature:",
                    mutableStateFlow = normalTempFlow,
                    valueRange = lowTemp.celsius..highTemp.celsius
                )
            }
        }
        item {
            val normalTemp by normalTempFlow.collectAsState()
            TemperatureSlider(
                "Low temperature:",
                mutableStateFlow = lowTempFlow,
                valueRange = 5f..normalTemp.celsius
            )
        }
    }
}

@Composable
private fun LowPressureSlider(
    mutableStateFlow: MutableStateFlow<Pressure>
) {
    val pressure by mutableStateFlow.collectAsState()
    Column {
        Row {
            Text(
                text = "Low pressure: ",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "%.2f bar".format(pressure.asBar()),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                "%.2f bar".format(0.5f),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                "%.2f bar".format(3f),
                Modifier.align(Alignment.TopEnd)
            )
        }
        Slider(
            value = pressure.asBar(),
            valueRange = 0.5f..3f,
            onValueChange = {
                mutableStateFlow.value = Pressure(it.times(100f).div(10f).roundToInt().times(10f))
            }
        )
        Spacer(modifier = Modifier.height(36.dp))
        Divider(thickness = Dp.Hairline)
        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun TemperatureSlider(
    title: String,
    mutableStateFlow: MutableStateFlow<Temperature>,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val temp by mutableStateFlow.collectAsState()
    Column {
        Row {
            Text(
                text = "$title ",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "%.1f°C".format(temp.celsius),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                "%.1f°C".format(valueRange.start),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                "%.1f°C".format(valueRange.endInclusive),
                Modifier.align(Alignment.TopEnd)
            )
        }
        Slider(
            value = temp.celsius,
            valueRange = valueRange,
            onValueChange = { mutableStateFlow.value = Temperature(it.roundToInt().toFloat()) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}