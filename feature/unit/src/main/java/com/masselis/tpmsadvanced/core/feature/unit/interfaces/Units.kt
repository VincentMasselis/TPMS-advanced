package com.masselis.tpmsadvanced.core.feature.unit.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.unit.ioc.FeatureUnitComponent
import com.masselis.tpmsadvanced.core.ui.EnumDropdown
import java.util.*

@Composable
public fun Units(modifier: Modifier = Modifier): Unit =
    Units(
        modifier,
        viewModel { FeatureUnitComponent.unitsViewModel },
    )

@Suppress("DEPRECATION")
@Composable
internal fun Units(
    modifier: Modifier = Modifier,
    viewModel: UnitsViewModel = viewModel { FeatureUnitComponent.unitsViewModel },
) {
    val pressure by viewModel.pressure.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        EnumDropdown(
            label = { Text("Pressure in") },
            stringOf = { it.string().capitalize(Locale.ROOT) },
            currentValue = pressure,
            onValue = { viewModel.pressure.value = it },
            modifier = Modifier.weight(1f),
        )
        EnumDropdown(
            label = { Text(text = "Temperature in") },
            stringOf = { it.string().capitalize(Locale.ROOT) },
            currentValue = temperature,
            onValue = { viewModel.temperature.value = it },
            modifier = Modifier.weight(1f),
        )
    }
}
