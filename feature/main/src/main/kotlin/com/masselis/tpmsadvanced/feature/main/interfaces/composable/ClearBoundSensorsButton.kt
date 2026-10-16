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
import com.masselis.tpmsadvanced.core.ui.key
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.ClearBoundSensorsButtonTags.root
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel.State
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
internal fun ClearBoundSensorsButton(
    modifier: Modifier = Modifier,
    component: VehicleComponent = LocalVehicleComponent.current,
    viewModel: ClearBoundSensorsViewModel = assistedMetroViewModel<ClearBoundSensorsViewModelImpl>(
        key = ClearBoundSensorsViewModelImpl::class.key(component.key)
    )
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
