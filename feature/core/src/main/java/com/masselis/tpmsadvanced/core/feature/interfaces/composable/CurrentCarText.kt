@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CarListViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.FavouriteCarViewModel
import com.masselis.tpmsadvanced.data.car.Car

@Composable
public fun CurrentCarText(
    modifier: Modifier = Modifier,
): Unit = CurrentCarText(modifier, viewModel { featureCoreComponent.favouriteCarViewModel })

@Composable
internal fun CurrentCarText(
    modifier: Modifier = Modifier,
    viewModel: FavouriteCarViewModel = viewModel { featureCoreComponent.favouriteCarViewModel }
) {
    val state by viewModel.stateFlow.collectAsState()
    val car = when (val state = state) {
        FavouriteCarViewModel.State.Loading -> return
        is FavouriteCarViewModel.State.CurrentCar -> state.car
    }
    var expanded by remember { mutableStateOf(false) }
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
        CarListDropdownMenu(
            isExpanded = expanded,
            onDismissRequest = { expanded = false },
            onNewFavourite = { viewModel.setFavourite(it) }
        )
    }
}

@Suppress("NAME_SHADOWING")
@Composable
private fun ExposedDropdownMenuBoxScope.CarListDropdownMenu(
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNewFavourite: (Car) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarListViewModel = viewModel { featureCoreComponent.carListViewModel }
) {
    val state by viewModel.stateFlow.collectAsState()
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.exposedDropdownSize(false)
    ) {
        state.list.forEach { currentCar ->
            DropdownMenuItem(
                text = { Text(currentCar.name, Modifier.weight(1f)) },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                onClick = {
                    onNewFavourite(currentCar)
                    onDismissRequest()
                },
            )
        }
        DropdownMenuItem(
            text = { Text(text = "Add a car", Modifier.weight(1f)) },
            trailingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = null) },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            onClick = onDismissRequest,
        )
    }
}
