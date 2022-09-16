package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PressureRangeSlider(
    onInfo: () -> Unit,
    pressures: ClosedFloatingPointRange<Pressure>,
    unit: PressureUnit,
    valueRange: ClosedFloatingPointRange<Pressure>,
    modifier: Modifier = Modifier,
    onValue: (ClosedFloatingPointRange<Pressure>) -> Unit,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Expected pressure range: ",
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${pressures.start.string(unit)} to ${pressures.endInclusive.string(unit)}",
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
        RangeSlider(
            value = pressures.start.convert(unit)..pressures.endInclusive.convert(unit),
            valueRange = valueRange.start.convert(unit)..valueRange.endInclusive.convert(unit),
            onValueChange = {
                onValue(
                    when (unit) {
                        PressureUnit.KILO_PASCAL -> it.start.kpa..it.endInclusive.kpa
                        PressureUnit.BAR -> it.start.bar..it.endInclusive.bar
                        PressureUnit.PSI -> it.start.psi..it.endInclusive.psi
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
    valueRange: ClosedFloatingPointRange<Temperature>,
    modifier: Modifier = Modifier,
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
