package com.masselis.tpmsadvanced.pecham_binding.interfaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.pecham_binding.interfaces.ListSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.usecase.BindTyreAndLocationToVehicleUseCase
import com.masselis.tpmsadvanced.pecham_binding.usecase.ListTyreUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

internal class ListSensorViewModelImpl @AssistedInject constructor(
    private val bindTyreAndLocationToVehicleUseCase: BindTyreAndLocationToVehicleUseCase,
    listTyreUseCase: ListTyreUseCase,
    unitPreferences: UnitPreferences,
    @Assisted private val vehicleUuid: UUID,
    @Assisted private val vehicleKind: Vehicle.Kind,
) : ViewModel(), ListSensorViewModel {

    @AssistedFactory
    interface Factory : (UUID, Vehicle.Kind) -> ListSensorViewModelImpl

    override val stateFlow = MutableStateFlow<State>(State.Empty)

    init {
        combine(
            listTyreUseCase(),
            unitPreferences.pressure,
            unitPreferences.temperature
        ) { (readyToBinds, bounds), pressureUnit, temperatureUnit ->
            if (readyToBinds.isEmpty() && bounds.isEmpty()) State.Empty
            else State.Tyres(vehicleKind, readyToBinds, bounds, pressureUnit, temperatureUnit)
        }.catch { State.Issue }
            .onEach { stateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun bind(location: Vehicle.Kind.Location, tyre: Tyre) {
        viewModelScope.launch {
            bindTyreAndLocationToVehicleUseCase(vehicleUuid, location, tyre)
        }
    }
}