package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.core.interfaces.composable.FavouriteButton
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.core.model.TyreLocation
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

@Preview
@Composable
fun FavouriteButtonPreview() {
    LazyColumn {
        items(
            listOf(
                mock(SensorFavouriteViewModel.State.Empty),
                mock(SensorFavouriteViewModel.State.RequestBond(420))
            )
        ) {
            FavouriteButton(
                tyreLocation = TyreLocation.FRONT_LEFT,
                viewModel = it
            )
        }
    }
}

private fun mock(state: SensorFavouriteViewModel.State) = Mockito
    .mock(SensorFavouriteViewModel::class.java).also {
        Mockito.`when`(it.stateFlow).thenReturn(MutableStateFlow(state))
    }