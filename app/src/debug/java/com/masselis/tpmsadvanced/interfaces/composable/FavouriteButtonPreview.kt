package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.masselis.tpmsadvanced.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.mocks
import com.masselis.tpmsadvanced.model.TyreLocation

@Preview
@Composable
fun FavouriteButtonPreview() {
    LazyColumn {
        items(SensorFavouriteViewModel.mocks()) {
            FavouriteButton(
                tyreLocation = TyreLocation.FRONT_LEFT,
                viewModel = it
            )
        }
    }
}