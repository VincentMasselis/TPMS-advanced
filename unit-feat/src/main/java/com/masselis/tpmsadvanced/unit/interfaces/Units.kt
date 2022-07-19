package com.masselis.tpmsadvanced.unit.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.uicommon.EnumDropdown
import com.masselis.tpmsadvanced.unit.model.PressureUnit
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit

@Composable
public fun Units(
    modifier: Modifier = Modifier
) = Units(modifier, viewModel { unitComponent.unitsViewModel })

@Suppress("DEPRECATION")
@Composable
internal fun Units(
    modifier: Modifier = Modifier,
    viewModel: UnitsViewModel
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        EnumDropdown(
            label = { Text("Pressure in") },
            stringOf = { it.string().capitalize() },
            values = PressureUnit.values(),
            mutableStateFlow = viewModel.pressure,
            modifier = Modifier.weight(1f),
        )
        EnumDropdown(
            label = { Text(text = "Temperature in") },
            stringOf = { it.string().capitalize() },
            values = TemperatureUnit.values(),
            mutableStateFlow = viewModel.temperature,
            modifier = Modifier.weight(1f),
        )
    }
}