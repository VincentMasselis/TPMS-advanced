package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt

@Composable
fun LowPressureSlider(
    onInfo: () -> Unit,
    mutableStateFlow: MutableStateFlow<Pressure>
) {
    val pressure by mutableStateFlow.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Low pressure: ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "%.2f bar".format(pressure.asBar()),
                fontWeight = FontWeight.Bold,
            )
            IconButton(
                onClick = onInfo,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                }
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
    }
}

@Composable
fun TemperatureSlider(
    onInfo: () -> Unit,
    title: String,
    mutableStateFlow: MutableStateFlow<Temperature>,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val temp by mutableStateFlow.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$title ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "%.1f°C".format(temp.celsius),
                fontWeight = FontWeight.Bold,
            )
            IconButton(
                onClick = onInfo,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                }
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
    }
}