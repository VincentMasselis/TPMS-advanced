package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreInfoButtonViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreInfoButtonViewModel.State
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreBindings.Companion.TyreInfoButtonViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent.Companion.TyreComponent

@Composable
internal fun TyreInfoButton(
    location: Location,
    modifier: Modifier = Modifier,
    viewmodel: TyreInfoButtonViewModel = LocalVehicleComponent.current
        .TyreComponent(location)
        .viewModel { TyreInfoButtonViewModel(createSavedStateHandle()) }
) {
    val state by viewmodel.stateFlow.collectAsState()
    TyreInfoButton(state, modifier)
}

@Composable
private fun TyreInfoButton(
    state: State,
    modifier: Modifier = Modifier,
) {
    var showTeslaTempDialog by rememberSaveable { mutableStateOf(false) }
    when (state) {
        State.AllClear -> {}
        State.SuspiciousTeslaFixedTemperature -> {
            IconButton(
                onClick = { showTeslaTempDialog = true },
                modifier = modifier
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.info_24px),
                    contentDescription = "Tyre info button",
                )
            }
        }
    }
    if (showTeslaTempDialog) {
        TeslaFixedTempDialog(
            onDismissRequest = { showTeslaTempDialog = false }
        )
    }
}

@Composable
private fun TeslaFixedTempDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Suspicious sensor behavior") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Image(
                        bitmap = ImageBitmap.imageResource(id = R.drawable.tesla_tpms),
                        contentDescription = "Tesla TPMS Sensor",
                    )
                }

                Spacer(Modifier.width(8.dp))
                Text(
                    "Your Tesla OEM-like sensor always returns the same temperature. This happens" +
                            " with some copies. If the temperature is critical for your" +
                            " needs, consider buying a new sensor like the OEM one or the Autel" +
                            " MX-Sensor BLE-A001, which was tested and approved.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("OK") } },
        modifier = modifier,
    )
}
