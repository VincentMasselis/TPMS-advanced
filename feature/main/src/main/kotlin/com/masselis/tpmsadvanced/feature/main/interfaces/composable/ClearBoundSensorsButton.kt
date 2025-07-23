package com.masselis.tpmsadvanced.feature.main.interfaces.composable

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
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.ClearBoundSensorsButtonTags.root
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel.State
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.InternalVehicleGraph
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleGraph

@Composable
internal fun ClearBoundSensorsButton(
    modifier: Modifier = Modifier,
    vehicleGraph: VehicleGraph = LocalVehicleGraph.current,
    viewModel: ClearBoundSensorsViewModelImpl = viewModel(
        key = "ClearBoundSensorsButton_${vehicleGraph.vehicle.uuid}"
    ) {
        (vehicleGraph as InternalVehicleGraph)
            .ClearBoundSensorsViewModel(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is State.ClearingPossible,
            onClick = { viewModel.clear() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag(root),
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.link_variant_remove), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Clear favourites")
        }
    }
}

@Suppress("ConstPropertyName")
internal object ClearBoundSensorsButtonTags {
    const val root = "ClearBoundSensorsButtonTags_root"
}
