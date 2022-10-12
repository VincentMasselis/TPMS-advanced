package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DemoTyreViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.Car
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.*

@Composable
internal fun PressureInfo(
    pressureRange: ClosedFloatingPointRange<Pressure>,
    unit: PressureUnit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Tyre(
                    location = TyreLocation.FRONT_LEFT,
                    modifier = Modifier.height(150.dp),
                    carComponent = DemoCarComponent(),
                    viewModel = DemoTyreViewModel(State.Alerting),
                )
                Text(
                    "If the pressure is below %s or above %s, the tyre starts to blink in red to alert you"
                        .format(
                            pressureRange.start.string(unit),
                            pressureRange.endInclusive.string(unit)
                        )
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

@Composable
internal fun TemperatureInfo(
    text: String,
    state: State,
    temperature: Temperature,
    unit: TemperatureUnit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Tyre(
                    location = TyreLocation.FRONT_LEFT,
                    modifier = Modifier.height(150.dp),
                    carComponent = DemoCarComponent(),
                    viewModel = DemoTyreViewModel(state),
                )
                Text(text.format(temperature.string(unit)))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "OK") } }
    )
}

private class DemoCarComponent : CarComponent() {
    override val findTyreComponentUseCase: FindTyreComponentUseCase
        get() = TODO("Not yet implemented")
    override val carId: UUID
        get() = TODO("Not yet implemented")
    override val carFlow: Flow<com.masselis.tpmsadvanced.data.car.model.Car>
        get() = TODO("Not yet implemented")
    override val scope: CoroutineScope
        get() = TODO("Not yet implemented")
    override val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
        get() = TODO("Not yet implemented")
    override val settingsViewModel: SettingsViewModel.Factory
        get() = TODO("Not yet implemented")
}
