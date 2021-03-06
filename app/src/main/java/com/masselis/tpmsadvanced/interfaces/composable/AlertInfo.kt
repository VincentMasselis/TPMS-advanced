package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.viewmodel.DemoTyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LowPressureInfo(
    lowPressureStateFlow: StateFlow<Pressure>,
    pressureUnitFlow: StateFlow<Pressure.Unit>,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val pressure by lowPressureStateFlow.collectAsState()
                val unit by pressureUnitFlow.collectAsState()
                Tyre(
                    location = TyreLocation.FRONT_LEFT,
                    modifier = Modifier.height(150.dp),
                    viewModel = DemoTyreViewModel(TyreViewModel.State.Alerting),
                )
                Text(
                    "When the pressure is < to %s, the tyre starts to blink in red to alert you"
                        .format(pressure.string(unit))
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

@Composable
fun TemperatureInfo(
    text: String,
    state: TyreViewModel.State,
    temperatureStateFlow: StateFlow<Temperature>,
    temperatureUnit: StateFlow<Temperature.Unit>,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val temperature by temperatureStateFlow.collectAsState()
                val unit by temperatureUnit.collectAsState()
                Tyre(
                    location = TyreLocation.FRONT_LEFT,
                    modifier = Modifier.height(150.dp),
                    viewModel = DemoTyreViewModel(state),
                )
                Text(text.format(temperature.string(unit)))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}