package com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.usecase.ListTyreUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class ListSensorViewModelImpl @AssistedInject constructor(
    listTyreUseCase: ListTyreUseCase,
    unitPreferences: UnitPreferences,
    @Assisted private val vehicleKind: Vehicle.Kind,
) : ViewModel(), ListSensorViewModel {

    @AssistedFactory
    interface Factory : (Vehicle.Kind) -> ListSensorViewModelImpl

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
}
