package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
    val carList by viewModel.carListFlow.collectAsState(emptyList())
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            modifier = Modifier.exposedDropdownSize(false)
        ) {
            carList.forEach { currentCar ->
                DropdownMenuItem(
                    text = { Text(currentCar.name, Modifier.weight(1f)) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        viewModel.setFavourite(currentCar)
                        expanded = false
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(text = "Add a car", Modifier.weight(1f)) },
                trailingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = null) },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                onClick = { expanded = false },
            )
        }
    }
}
