package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

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
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.usecase.BindSensorToVehicleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

@Suppress("NAME_SHADOWING")
internal class BindSensorViewModelImpl @AssistedInject constructor(
    private val bindSensorToVehicleUseCase: BindSensorToVehicleUseCase,
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
    @Assisted private val vehicleUuid: UUID,
    @Assisted private val tyre: Tyre,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel(), BindSensorViewModel {

    @AssistedFactory
    interface Factory : (UUID, Tyre, SavedStateHandle) -> BindSensorViewModelImpl

    override val stateFlow: StateFlow<State>

    init {
        val currentVehicle = vehicleDatabase.selectByUuid(vehicleUuid)
        val knownSensors = sensorDatabase.selectListByVehicleId(vehicleUuid)
        val boundVehicle = vehicleDatabase.selectBySensorId(tyre.id)
        val boundVehicleLocation = sensorDatabase.selectById(tyre.id)
        stateFlow = savedStateHandle.getMutableStateFlow("STATE") {
            computeState(
                currentVehicle.value,
                knownSensors.value,
                boundVehicle.value,
                boundVehicleLocation.value?.location
            )
        }
        combine(
            currentVehicle,
            knownSensors,
            boundVehicle,
            boundVehicleLocation
        ) { currentVehicle, knownSensors, boundVehicle, boundVehicleLocation ->
            computeState(currentVehicle, knownSensors, boundVehicle, boundVehicleLocation?.location)
        }.onEach { stateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun bind(location: Location) {
        viewModelScope.launch(NonCancellable) {
            bindSensorToVehicleUseCase.bind(vehicleUuid, Sensor(tyre.id, location), tyre)
        }
    }

    private fun computeState(
        currentVehicle: Vehicle,
        alreadyBoundToVehicleList: List<Sensor>,
        boundVehicle: Vehicle?,
        boundVehicleLocation: Location?,
    ): State =
        if (boundVehicle != null && boundVehicleLocation != null)
            State.BoundToAnOtherVehicle(
                currentVehicle,
                alreadyBoundToVehicleList.map { it.location }.toSet(),
                boundVehicle,
                boundVehicleLocation
            )
        else
            State.ReadyToBind(
                currentVehicle,
                alreadyBoundToVehicleList.map { it.location }.toSet()
            )
}