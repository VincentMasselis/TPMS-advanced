package com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BindSensorToVehicleUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BoundSensorUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.SearchingUnlocatedTyresUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.VehicleBindingStatusUseCase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import java.util.UUID

@Suppress("LongParameterList")
@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class ListSensorViewModelImpl(
    private val vehicleBindingStatusUseCase: VehicleBindingStatusUseCase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val unitPreferences: UnitPreferences,
    private val bindSensorToVehicleUseCase: BindSensorToVehicleUseCase,
    searchingUnlocatedTyresUseCaseFactory: SearchingUnlocatedTyresUseCase.Factory,
    boundSensorUseCaseFactory: BoundSensorUseCase.Factory,
    @Assisted private val vehicleUuid: UUID,
) : ViewModel(), ListSensorViewModel {

    @AssistedFactory
    interface Factory : (UUID) -> ListSensorViewModelImpl

    private val searchingUnlocatedTyresUseCase = searchingUnlocatedTyresUseCaseFactory(vehicleUuid)
    private val boundSensorUseCase = boundSensorUseCaseFactory(vehicleUuid)
    override val stateFlow = MutableStateFlow(
        boundSensorUseCase.everyWheelIsAlreadyBound()
            ?.let { State.AllWheelsAreAlreadyBound(it) }
            ?: State.UnplugEverySensor
    )
    private var searchJob: Job? = null

    override fun acknowledgeAndClearBinding() {
        if (stateFlow.value !is State.AllWheelsAreAlreadyBound)
            return
        viewModelScope.launch { bindSensorToVehicleUseCase.clearBindings(vehicleUuid) }
        stateFlow.value = State.UnplugEverySensor
    }

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
                emit(searching as State)
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
        .catch {
            Firebase.crashlytics.recordException(it)
            emit(State.Issue)
        }
        .onEach { stateFlow.value = it }
        .launchIn(viewModelScope)
        .also {
            searchJob?.cancel()
            searchJob = it
        }
}
