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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.ClearBoundSensorsButtonTags.tag
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent

@Composable
internal fun ClearBoundSensorsButton(
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: ClearBoundSensorsViewModel = viewModel(
        key = "ClearBoundSensorsButton_${vehicleComponent.vehicle.uuid}"
    ) {
        vehicleComponent.clearBoundSensorsViewModel.build(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is ClearBoundSensorsViewModel.State.ClearingPossible,
            onClick = { viewModel.clear() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag(tag),
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.link_variant_remove), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Clear favourites")
        }
    }
}

public object ClearBoundSensorsButtonTags {
    public const val tag: String = "ClearBoundSensorsButtonTags_tag"
}
