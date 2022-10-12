package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent

@Composable
internal fun ClearFavourites(
    modifier: Modifier = Modifier,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: ClearFavouritesViewModel = viewModel(key = "ClearFavouritesViewModel_${carComponent.carId}") {
        carComponent.clearFavouritesViewModel.build(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is ClearFavouritesViewModel.State.ClearingPossible,
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { viewModel.clear() }
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.link_variant_remove), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Clear favourites")
        }
    }
}
