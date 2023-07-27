package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
                text = AnnotatedString(
                    "Expected pressure range: ",
                    SpanStyle(fontWeight = FontWeight.Medium)
                ) + AnnotatedString(
                    "${pressures.start.string(unit)} to ${pressures.endInclusive.string(unit)}",
                    SpanStyle(fontWeight = FontWeight.Bold)
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onInfo,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "More information about this alert"
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
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = AnnotatedString(
                    "$title ",
                    SpanStyle(fontWeight = FontWeight.Medium)
                ) + AnnotatedString(
                    temperature.string(unit),
                    SpanStyle(fontWeight = FontWeight.Bold)
                ),
                Modifier.weight(1f)
            )
            IconButton(
                onClick = onInfo,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "More information about this alert"
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
