package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.UnitsViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.uicommon.EnumDropdown

@Suppress("DEPRECATION")
@Composable
fun Units(
    modifier: Modifier = Modifier,
    viewModel: UnitsViewModel = viewModel { mainComponent.unitsViewModel }
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        EnumDropdown(
            label = { Text("Pressure in") },
            stringOf = { it.string().capitalize() },
            values = Pressure.Unit.values(),
            mutableStateFlow = viewModel.pressure,
            modifier = Modifier.weight(1f),
        )
        EnumDropdown(
            label = { Text(text = "Temperature in") },
            stringOf = { it.string().capitalize() },
            values = Temperature.Unit.values(),
            mutableStateFlow = viewModel.temperature,
            modifier = Modifier.weight(1f),
        )
    }
}