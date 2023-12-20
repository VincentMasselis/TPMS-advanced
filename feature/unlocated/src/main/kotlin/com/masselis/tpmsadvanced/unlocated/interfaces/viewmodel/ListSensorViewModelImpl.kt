package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.usecase.SearchingUnlocatedTyresUseCase
import com.masselis.tpmsadvanced.unlocated.usecase.VehicleBindingStatusUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class ListSensorViewModelImpl @AssistedInject constructor(
    private val vehicleBindingStatusUseCase: VehicleBindingStatusUseCase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val unitPreferences: UnitPreferences,
    searchingUnlocatedTyresUseCaseFactory: SearchingUnlocatedTyresUseCase.Factory,
    @Assisted private val vehicleUuid: UUID,
) : ViewModel(), ListSensorViewModel {

    @AssistedFactory
    interface Factory : (UUID) -> ListSensorViewModelImpl

    private val searchingUnlocatedTyresUseCase = searchingUnlocatedTyresUseCaseFactory(vehicleUuid)
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
        currentVehicleUseCase.flatMapLatest { it.vehicleStateFlow },
        searchingUnlocatedTyresUseCase.search(),
        unitPreferences.pressure,
        unitPreferences.temperature,
        vehicleBindingStatusUseCase.areAllWheelBound(vehicleUuid),
    ) { vehicle, (boundSensorToCurrentVehicle, unboundTyres, boundTyresToOtherVehicle), pressureUnit, temperatureUnit, allWheelsBound ->
        State.Searching(
            vehicle.name,
            vehicle.kind,
            boundSensorToCurrentVehicle,
            unboundTyres,
            boundTyresToOtherVehicle,
            pressureUnit,
            temperatureUnit,
            allWheelsBound,
            unboundTyres.isEmpty(),
        )
    }.catch { State.Issue }
        .onEach { stateFlow.value = it }
        .launchIn(viewModelScope)
        .also {
            searchJob?.cancel()
            searchJob = it
        }
}
