package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.masselis.tpmsadvanced.interfaces.initializer.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.model.TyreLocation

@Composable
fun Tyre(
    location: TyreLocation,
    modifier: Modifier = Modifier,
    viewModel: TyreViewModel = savedStateViewModel(key = "TyreViewModel_${location.name}") {
        mainComponent.findTyreComponent.find(location).tyreViewModelFactory.build(it)
    },
) {
    Box(
        modifier
            .clip(RoundedCornerShape(percent = 20))
            .aspectRatio(15f / 40f)
            .background(Color.Green)
    )
}