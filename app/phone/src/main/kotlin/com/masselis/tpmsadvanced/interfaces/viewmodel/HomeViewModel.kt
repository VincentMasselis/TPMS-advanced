package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

@AssistedInject
internal class HomeViewModel(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    @Assisted expectedVehicle: UUID?,
) : ViewModel() {

    @AssistedFactory
    interface Factory : (UUID?) -> HomeViewModel

    init {
        expectedVehicle?.also {
            viewModelScope.launch {
                currentVehicleUseCase.setAsCurrent(it)
            }
        }
    }

    val vehicleComponentStateFlow: StateFlow<VehicleComponent> = currentVehicleUseCase
}
