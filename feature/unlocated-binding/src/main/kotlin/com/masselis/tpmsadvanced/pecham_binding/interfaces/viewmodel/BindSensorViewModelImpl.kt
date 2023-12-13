package com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.BindSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.usecase.BindSensorToVehicleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BindSensorViewModelImpl @AssistedInject constructor(
    private val bindSensorToVehicleUseCase: BindSensorToVehicleUseCase,
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
    @Assisted private val vehicle: Vehicle,
    @Assisted private val tyre: Tyre,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel(), BindSensorViewModel {

    @AssistedFactory
    interface Factory : (Vehicle, Tyre, SavedStateHandle) -> BindSensorViewModelImpl

    override val stateFlow = savedStateHandle.getMutableStateFlow("STATE") {
        computeState(
            sensorDatabase.selectListByVehicleId(vehicle.uuid),
            vehicleDatabase.selectBySensorId(tyre.id),
            sensorDatabase.selectById(tyre.id)?.location
        )
    }

    init {
        // TODO Put this into a use case
        combine(
            sensorDatabase.selectListByVehicleIdFlow(vehicle.uuid),
            vehicleDatabase.selectBySensorIdFlow(tyre.id),
            sensorDatabase.selectByIdFlow(tyre.id)
        )   { knownSensors, alreadyBoundVehicle, alreadyBoundSensor ->
            computeState(knownSensors, alreadyBoundVehicle, alreadyBoundSensor?.location)
        }.onEach { stateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun bind(location: Location) {
        viewModelScope.launch(NonCancellable) {
            bindSensorToVehicleUseCase.bind(vehicle.uuid, Sensor(tyre.id, location), tyre)
        }
    }

    private fun computeState(
        alreadyBoundToVehicleList: List<Sensor>,
        currentVehicle: Vehicle?,
        currentLocation: Location?,
    ): State {
        return if (currentVehicle != null && currentLocation != null)
            State.BoundToAnOtherVehicle(
                alreadyBoundToVehicleList.map { it.location }.toSet(),
                currentVehicle,
                currentLocation
            )
        else
            State.ReadyToBind(
                alreadyBoundToVehicleList.map { it.location }.toSet()
            )
    }
}