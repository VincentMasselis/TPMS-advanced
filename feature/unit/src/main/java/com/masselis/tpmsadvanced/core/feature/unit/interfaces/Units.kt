package com.masselis.tpmsadvanced.core.feature.unit.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.unit.ioc.featureUnitComponent
import com.masselis.tpmsadvanced.core.ui.EnumDropdown
import java.util.*

@Composable
public fun Units(modifier: Modifier = Modifier): Unit =
    Units(viewModel { featureUnitComponent.unitsViewModel }, modifier)

@Suppress("DEPRECATION")
@Composable
internal fun Units(
    viewModel: UnitsViewModel,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        EnumDropdown(
            label = { Text("Pressure in") },
            stringOf = { it.string().capitalize(Locale.ROOT) },
            mutableStateFlow = viewModel.pressure,
            modifier = Modifier.weight(1f),
        )
        EnumDropdown(
            label = { Text(text = "Temperature in") },
            stringOf = { it.string().capitalize(Locale.ROOT) },
            mutableStateFlow = viewModel.temperature,
            modifier = Modifier.weight(1f),
        )
    }
}
