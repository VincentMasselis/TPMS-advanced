package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.usecase.ListTyreUseCase
import com.masselis.tpmsadvanced.unlocated.usecase.VehicleBindingStatusUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

internal class ListSensorViewModelImpl @AssistedInject constructor(
    private val listTyreUseCase: ListTyreUseCase,
    private val vehicleBindingStatusUseCase: VehicleBindingStatusUseCase,
    private val unitPreferences: UnitPreferences,
    @Assisted private val vehicle: Vehicle,
) : ViewModel(), ListSensorViewModel {

    @AssistedFactory
    interface Factory : (Vehicle) -> ListSensorViewModelImpl

    override val stateFlow = MutableStateFlow<State>(State.UnplugEverySensor)

    private var searchJob: Job? = null

    override fun acknowledgeSensorUnplugged() {
        if (stateFlow.value !is State.UnplugEverySensor)
            return
        search()
    }

    override fun onSensorBound() {
        if (stateFlow.value !is State.Searching)
            return
        search()
    }

    private fun search() = combine(
        listTyreUseCase(),
        unitPreferences.pressure,
        unitPreferences.temperature,
        vehicleBindingStatusUseCase.areAllWheelBound(vehicle),
    ) { (readyToBinds, bounds), pressureUnit, temperatureUnit, allWheelsBound ->
        State.Searching(
            if (readyToBinds.isEmpty()) State.Searching.ShowPlaceholder
            else State.Searching.ShowList(readyToBinds),
            bounds,
            pressureUnit,
            temperatureUnit,
            allWheelsBound
        )
    }.catch { State.Issue }
        .onEach { stateFlow.value = it }
        .launchIn(viewModelScope)
        .also {
            searchJob?.cancel()
            searchJob = it
        }
}
