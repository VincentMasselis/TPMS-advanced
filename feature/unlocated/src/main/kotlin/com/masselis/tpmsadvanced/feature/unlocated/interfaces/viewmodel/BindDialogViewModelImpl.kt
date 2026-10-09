package com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.common.combineStates
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.BindDialogViewModel.State
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BindSensorToVehicleUseCase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID

@Suppress("NAME_SHADOWING")
@AssistedInject
internal class BindDialogViewModelImpl(
    private val bindSensorToVehicleUseCase: BindSensorToVehicleUseCase,
    sensorDatabase: SensorDatabase,
    vehicleDatabase: VehicleDatabase,
    @Assisted private val vehicleUuid: UUID,
    @Assisted private val tyre: Tyre,
) : ViewModel(), BindDialogViewModel {

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            vehicleUuid: UUID,
            tyre: Tyre,
        ): BindDialogViewModelImpl
    }

    override val stateFlow: StateFlow<State> = combineStates(
        vehicleDatabase.selectByUuid(vehicleUuid).asStateFlow(viewModelScope + IO, Eagerly),
        sensorDatabase.selectListByVehicleId(vehicleUuid).asStateFlow(viewModelScope + IO, Eagerly),
        vehicleDatabase.selectBySensorId(tyre.sensorId).asStateFlow(viewModelScope + IO, Eagerly),
        sensorDatabase.selectById(tyre.sensorId).asStateFlow(viewModelScope + IO, Eagerly)
    ) { currentVehicle, knownSensors, boundVehicle, boundVehicleLocation ->
        if (boundVehicle != null && boundVehicleLocation != null)
            State.BoundToAnOtherVehicle(
                currentVehicle,
                knownSensors.map { it.location }.toSet(),
                boundVehicle,
                boundVehicleLocation.location
            )
        else
            State.ReadyToBind(
                currentVehicle,
                knownSensors.map { it.location }.toSet()
            )
    }

    override fun bind(location: Location) {
        viewModelScope.launch {
            bindSensorToVehicleUseCase.bind(vehicleUuid, Sensor(tyre.sensorId, location), tyre)
        }
    }
}
