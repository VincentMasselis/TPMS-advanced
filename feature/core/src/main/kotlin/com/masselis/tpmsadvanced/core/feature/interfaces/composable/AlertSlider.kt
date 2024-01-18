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
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.fahrenheit

@Composable
internal fun PressureRangeSlider(
    minMaxRange: ClosedFloatingPointRange<Pressure>,
    values: ClosedFloatingPointRange<Pressure>,
    onValue: (ClosedFloatingPointRange<Pressure>) -> Unit,
    openInfo: () -> Unit,
    unit: PressureUnit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = AnnotatedString(
                    "Expected pressure range: ",
                    SpanStyle(fontWeight = FontWeight.Medium)
                ) + AnnotatedString(
                    "${values.start.string(unit)} to ${values.endInclusive.string(unit)}",
                    SpanStyle(fontWeight = FontWeight.Bold)
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = openInfo,
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
                minMaxRange.start.string(unit),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                minMaxRange.endInclusive.string(unit),
                Modifier.align(Alignment.TopEnd)
            )
        }
        RangeSlider(
            value = values.start.convert(unit)..values.endInclusive.convert(unit),
            valueRange = minMaxRange.start.convert(unit)..minMaxRange.endInclusive.convert(unit),
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

@Preview
@Composable
internal fun PressureRangeSliderPreview() {
    PressureRangeSlider(
        values = 1.5f.bar..2.5f.bar,
        minMaxRange = 0.5f.bar..5f.bar,
        onValue = {},
        openInfo = {},
        unit = PressureUnit.BAR,
    )
}

@Composable
internal fun TemperatureSlider(
    title: String,
    minMaxRange: ClosedFloatingPointRange<Temperature>,
    value: Temperature,
    onValue: (Temperature) -> Unit,
    openInfo: () -> Unit,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = AnnotatedString(
                    "$title ",
                    SpanStyle(fontWeight = FontWeight.Medium)
                ) + AnnotatedString(
                    value.string(unit),
                    SpanStyle(fontWeight = FontWeight.Bold)
                ),
                Modifier.weight(1f)
            )
            IconButton(
                onClick = openInfo,
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
                minMaxRange.start.string(unit),
                Modifier.align(Alignment.TopStart)
            )
            Text(
                minMaxRange.endInclusive.string(unit),
                Modifier.align(Alignment.TopEnd)
            )
        }
        Slider(
            value = value.convert(unit),
            valueRange = minMaxRange.start.convert(unit)..minMaxRange.endInclusive.convert(unit),
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

@Preview
@Composable
internal fun TemperatureSliderPreview() {
    TemperatureSlider(
        title = "Normal temp",
        minMaxRange = 5f.celsius..80f.celsius,
        value = 20f.celsius,
        onValue = {},
        openInfo = { },
        unit = TemperatureUnit.CELSIUS
    )
}
