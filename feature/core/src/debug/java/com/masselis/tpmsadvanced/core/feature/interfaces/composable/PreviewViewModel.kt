@file:Suppress("MagicNumber")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

// ------------- Tyre component

internal fun previewTyreViewModelImpl(
    state: TyreViewModel.State = TyreViewModel.State.Normal.BlueToGreen(Fraction(0.5f))
) = mockk<TyreViewModelImpl> {
    every { stateFlow } returns MutableStateFlow(state)
}

internal val previewTyreViewModelStates = listOf(
    TyreViewModel.State.NotDetected,
    TyreViewModel.State.Normal.BlueToGreen(Fraction(0f)),
    TyreViewModel.State.Normal.BlueToGreen(Fraction(0.5f)),
    TyreViewModel.State.Normal.BlueToGreen(Fraction(1f)),
    TyreViewModel.State.Normal.GreenToRed(Fraction(0f)),
    TyreViewModel.State.Normal.GreenToRed(Fraction(0.5f)),
    TyreViewModel.State.Normal.GreenToRed(Fraction(1f)),
    TyreViewModel.State.Alerting,
)

internal fun previewTyreStatViewModel(
    state: TyreStatsViewModel.State = TyreStatsViewModel.State.Normal(
        1.5f.bar,
        BAR,
        45f.celsius,
        CELSIUS
    )
) = mockk<TyreStatsViewModel> {
    every { stateFlow } returns MutableStateFlow(state)
}

internal val previewTyreStatViewModelStates = listOf(
    TyreStatsViewModel.State.NotDetected,
    TyreStatsViewModel.State.Normal(2f.bar, BAR, 30f.celsius, CELSIUS),
    TyreStatsViewModel.State.Alerting(0.5f.bar, BAR, 150f.celsius, CELSIUS),
)

// ------------- Vehicle component

internal fun previewBindSensorViewModel(
    state: BindSensorButtonViewModel.State =
        BindSensorButtonViewModel.State.RequestBond.NewBinding(previewSensor)
) = mockk<BindSensorButtonViewModel> {
    every { stateFlow } returns MutableStateFlow(state)
}

internal fun previewClearBoundSensorsViewModel(
    state: ClearBoundSensorsViewModel.State = ClearBoundSensorsViewModel.State.ClearingPossible
) = mockk<ClearBoundSensorsViewModel> {
    every { stateFlow } returns MutableStateFlow(state)
}

internal fun previewVehicleSettingsViewModel(
    lowPressure: Pressure = 0.5f.bar,
    highPressure: Pressure = 2.5f.bar,
    pressureUnit: PressureUnit = BAR,
    highTemp: Temperature = 90f.celsius,
    normalTemp: Temperature = 45f.celsius,
    lowTemp: Temperature = 25f.celsius,
    temperatureUnit: TemperatureUnit = CELSIUS,
) = mockk<VehicleSettingsViewModel>().also {
    every { it.lowPressure } returns MutableStateFlow(lowPressure)
    every { it.highPressure } returns MutableStateFlow(highPressure)
    every { it.pressureUnit } returns MutableStateFlow(pressureUnit)
    every { it.highTemp } returns MutableStateFlow(highTemp)
    every { it.normalTemp } returns MutableStateFlow(normalTemp)
    every { it.lowTemp } returns MutableStateFlow(lowTemp)
    every { it.temperatureUnit } returns MutableStateFlow(temperatureUnit)
}

internal fun previewDeleteVehicleViewModel(
    state: DeleteVehicleViewModel.State =
        DeleteVehicleViewModel.State.DeletableVehicle(previewVehicle)
) = mockk<DeleteVehicleViewModel> {
    every { stateFlow } returns MutableStateFlow(state)
}

// ------------- Feature core component

internal fun previewCurrentVehicleComponent(
    component: VehicleComponent = PreviewVehicleComponent()
) = mockk<CurrentVehicleComponentViewModel> {
    every { stateFlow } returns MutableStateFlow(component)
}

internal fun previewCurrentVehicleDropdownViewModel(
    state: CurrentVehicleDropdownViewModel.State = CurrentVehicleDropdownViewModel.State.Vehicles(
        previewVehicle,
        listOf(
            previewVehicle,
            previewVehicle.copy(uuid = UUID.randomUUID(), kind = Kind.MOTORCYCLE, name = "PREVIEW 2")
        )
    )
) = mockk<CurrentVehicleDropdownViewModel> {
    every { stateFlow } returns MutableStateFlow(state)
}
