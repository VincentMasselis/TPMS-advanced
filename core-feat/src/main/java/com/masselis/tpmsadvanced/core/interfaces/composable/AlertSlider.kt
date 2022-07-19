package com.masselis.tpmsadvanced.core.interfaces.composable

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
import com.masselis.tpmsadvanced.core.model.Pressure
import com.masselis.tpmsadvanced.core.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.core.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.core.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.core.model.Temperature
import com.masselis.tpmsadvanced.core.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.core.model.Temperature.CREATOR.fahrenheit
import com.masselis.tpmsadvanced.unit.model.PressureUnit
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LowPressureSlider(
    onInfo: () -> Unit,
    mutableStateFlow: MutableStateFlow<Pressure>,
    unitFlow: StateFlow<PressureUnit>
) {
    val pressure by mutableStateFlow.collectAsState()
    val unit by unitFlow.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Low pressure: ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = pressure.string(unit),
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
                0.5f.bar.string(unit),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                5f.bar.string(unit),
                Modifier.align(Alignment.TopEnd)
            )
        }
        Slider(
            value = pressure.convert(unit),
            valueRange = 0.5f.bar.convert(unit)..5f.bar.convert(unit),
            onValueChange = {
                mutableStateFlow.value = when (unit) {
                    PressureUnit.KILO_PASCAL -> it.kpa
                    PressureUnit.BAR -> it.bar
                    PressureUnit.PSI -> it.psi
                }
            }
        )
    }
}

@Composable
fun TemperatureSlider(
    onInfo: () -> Unit,
    title: String,
    mutableStateFlow: MutableStateFlow<Temperature>,
    unitFlow: StateFlow<TemperatureUnit>,
    valueRange: ClosedFloatingPointRange<Temperature>
) {
    val temp by mutableStateFlow.collectAsState()
    val unit by unitFlow.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$title ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = temp.string(unit),
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
                valueRange.start.string(unit),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                valueRange.endInclusive.string(unit),
                Modifier.align(Alignment.TopEnd)
            )
        }
        Slider(
            value = temp.convert(unit),
            valueRange = valueRange.start.convert(unit)..valueRange.endInclusive.convert(unit),
            onValueChange = {
                mutableStateFlow.value =
                    when (unit) {
                        TemperatureUnit.CELSIUS -> it.celsius
                        TemperatureUnit.FAHRENHEIT -> it.fahrenheit
                    }
            }
        )
    }
}