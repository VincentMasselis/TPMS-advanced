package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
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
import kotlinx.coroutines.flow.transformWhile
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

    @Suppress("MaxLineLength")
    private fun search() = combine(
        currentVehicleUseCase.flatMapLatest { it.vehicleStateFlow },
        searchingUnlocatedTyresUseCase.search(),
        unitPreferences.pressure,
        unitPreferences.temperature,
    ) { vehicle, (boundSensorToCurrentVehicle, unboundTyres, boundTyresToOtherVehicle), pressureUnit, temperatureUnit ->
        State.Searching(
            vehicle.name,
            vehicle.kind,
            boundSensorToCurrentVehicle,
            unboundTyres,
            boundTyresToOtherVehicle,
            pressureUnit,
            temperatureUnit,
        )
    }.combine(vehicleBindingStatusUseCase.boundLocations(vehicleUuid)) { a, b -> a to b }
        .transformWhile { (searching, boundLocations) ->
            val (kind, boundSensors) = boundLocations
            if (kind.locations.subtract(boundSensors.map { it.location }.toSet()).isNotEmpty()) {
                emit(searching)
                true
            } else {
                emit(
                    State.Completed(
                        searching.currentVehicleName,
                        searching.currentVehicleKind,
                        boundSensors.map { it to null },
                        searching.pressureUnit,
                        searching.temperatureUnit
                    )
                )
                false
            }
        }
        .catch { State.Issue }
        .onEach { stateFlow.value = it }
        .launchIn(viewModelScope)
        .also {
            searchJob?.cancel()
            searchJob = it
        }
}
