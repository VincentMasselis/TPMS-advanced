package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentCarViewModel
import com.masselis.tpmsadvanced.data.car.Car
import java.util.*

@Composable
public fun CurrentCarText(
    modifier: Modifier = Modifier,
): Unit = CurrentCarText(modifier, featureCoreComponent.currentCarViewModel)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrentCarText(
    modifier: Modifier = Modifier,
    viewModel: CurrentCarViewModel = featureCoreComponent.currentCarViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val car by viewModel.flow.collectAsState()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.menuAnchor()
        ) {
            Text(text = car.name)
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded }
        ) {
            listOf(car, Car(UUID.randomUUID(), "toto", false)).forEach { selectedValue ->
                DropdownMenuItem(
                    text = { Text(selectedValue.name) },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }
    }
}
