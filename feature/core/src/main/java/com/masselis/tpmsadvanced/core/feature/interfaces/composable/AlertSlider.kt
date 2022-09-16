package com.masselis.tpmsadvanced.core.feature.interfaces.composable

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.fahrenheit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit

@Composable
public fun LowPressureSlider(
    currentValue: Pressure,
    unit: PressureUnit,
    onInfo: () -> Unit,
    onValue: (Pressure) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Low pressure: ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = currentValue.string(unit),
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
            value = currentValue.convert(unit),
            valueRange = 0.5f.bar.convert(unit)..5f.bar.convert(unit),
            onValueChange = {
                onValue(
                    when (unit) {
                        PressureUnit.KILO_PASCAL -> it.kpa
                        PressureUnit.BAR -> it.bar
                        PressureUnit.PSI -> it.psi
                    }
                )
            }
        )
    }
}

@Composable
internal fun TemperatureSlider(
    onInfo: () -> Unit,
    title: String,
    temperature: Temperature,
    unit: TemperatureUnit,
    onValue: (Temperature) -> Unit,
    valueRange: ClosedFloatingPointRange<Temperature>
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$title ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = temperature.string(unit),
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
            value = temperature.convert(unit),
            valueRange = valueRange.start.convert(unit)..valueRange.endInclusive.convert(unit),
            onValueChange = {
                onValue(
                    when (unit) {
                        TemperatureUnit.CELSIUS -> it.celsius
                        TemperatureUnit.FAHRENHEIT -> it.fahrenheit
                    }
                )
            }
        )
    }
}
