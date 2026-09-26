package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.KILO_PASCAL
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.PSI
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.PressureCalibration
import com.masselis.tpmsadvanced.feature.main.R
import kotlin.math.roundToInt

/**
 * Corrects the pressure read by sensors which are a bit off: a switch turning the calibration on,
 * then the offset and multiplier sliders while it's on. [example] is a pressure the user expects
 * their tyres to be at, used to show what the correction does.
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PressureCalibrationSettings(
    enabled: Boolean,
    calibration: PressureCalibration,
    example: Pressure,
    unit: PressureUnit,
    onEnabled: (Boolean) -> Unit,
    onOffset: (Pressure) -> Unit,
    onMultiplier: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The explanation of whichever slider's info button was tapped, null while no dialog is open
    var info: String? by remember { mutableStateOf(null) }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Pressure calibration",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = enabled, onCheckedChange = onEnabled)
        }
        if (enabled) {
            CalibrationSlider(
                title = "Offset: ",
                valueText = calibration.offset.signedString(unit),
                bounds = (-OffsetLimit).string(unit) to OffsetLimit.string(unit),
                value = calibration.offset.convert(unit),
                valueRange = (-OffsetLimit).convert(unit)..OffsetLimit.convert(unit),
                // Snapped to a gauge's resolution, a raw slider value would read like 1.4837 psi
                onValue = { onOffset(it.roundTo(unit.offsetStep).toPressure(unit)) },
                openInfo = {
                    info = "The offset is added to the pressure a sensor sends, after the multiplier. " +
                        "Use it for a sensor which reads the same amount too high or too low at any " +
                        "pressure.\n\n" +
                        "A calibrated pressure is marked with an asterisk, like ${example.string(unit)}*."
                },
            )
            CalibrationSlider(
                title = "Multiplier: ",
                valueText = calibration.multiplier.multiplierString(),
                bounds = MultiplierLimits.start.multiplierString() to MultiplierLimits.endInclusive.multiplierString(),
                value = calibration.multiplier,
                valueRange = MultiplierLimits,
                onValue = { onMultiplier(it.roundTo(MULTIPLIER_STEP)) },
                openInfo = {
                    info = "The pressure a sensor sends is multiplied by the multiplier, before the " +
                        "offset is added. Use it for a sensor whose error grows with the pressure.\n\n" +
                        "A calibrated pressure is marked with an asterisk, like ${example.string(unit)}*."
                },
            )
            Text(
                "Example: ${example.string(unit)} read by a sensor is shown as " +
                    "${calibration.applyTo(example).string(unit)}*"
            )
        }
    }
    info?.let { text ->
        AlertDialog(
            text = { Text(text) },
            onDismissRequest = { info = null },
            confirmButton = { TextButton(onClick = { info = null }) { Text(text = "OK") } }
        )
    }
}

@Composable
private fun CalibrationSlider(
    title: String,
    valueText: String,
    bounds: Pair<String, String>,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValue: (Float) -> Unit,
    openInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = AnnotatedString(title, SpanStyle(fontWeight = FontWeight.Medium)) +
                    AnnotatedString(valueText, SpanStyle(fontWeight = FontWeight.Bold)),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = openInfo,
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.info_24px),
                        contentDescription = "More information about the ${title.trimEnd(' ', ':').lowercase()}"
                    )
                }
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(bounds.first, Modifier.align(Alignment.TopStart))
            Text(bounds.second, Modifier.align(Alignment.TopEnd))
        }
        Slider(value = value, valueRange = valueRange, onValueChange = onValue)
    }
}

/** A few units at most for a sensor which is a bit off, a sensor further off is broken */
private val OffsetLimit = 10f.psi

@Suppress("MagicNumber")
private val MultiplierLimits = 0.8f..1.2f

private const val MULTIPLIER_STEP = 0.01f

private operator fun Pressure.unaryMinus() = (-kpa).kpa

private fun Pressure.signedString(unit: PressureUnit) = "${if (kpa > 0f) "+" else ""}${string(unit)}"

private fun Float.multiplierString() = "×%.2f".format(this)

private fun Float.roundTo(step: Float) = (this / step).roundToInt() * step

private fun Float.toPressure(unit: PressureUnit) = when (unit) {
    KILO_PASCAL -> kpa
    BAR -> bar
    PSI -> psi
}

/** A gauge's resolution in each unit */
@Suppress("MagicNumber")
private val PressureUnit.offsetStep: Float
    get() = when (this) {
        KILO_PASCAL -> 5f
        BAR -> 0.05f
        PSI -> 0.5f
    }

@Preview
@Composable
internal fun PressureCalibrationSettingsPreview() {
    PressureCalibrationSettings(
        enabled = true,
        calibration = PressureCalibration(0.1f.bar, 1.02f),
        example = 2.4f.bar,
        unit = BAR,
        onEnabled = {},
        onOffset = {},
        onMultiplier = {},
    )
}

@Preview
@Composable
internal fun PressureCalibrationSettingsOffPreview() {
    PressureCalibrationSettings(
        enabled = false,
        calibration = PressureCalibration(0f.kpa, 1f),
        example = 35f.psi,
        unit = PSI,
        onEnabled = {},
        onOffset = {},
        onMultiplier = {},
    )
}
