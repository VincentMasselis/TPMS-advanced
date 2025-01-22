package com.masselis.tpmsadvanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

internal class HomeViewModel @AssistedInject constructor(
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
