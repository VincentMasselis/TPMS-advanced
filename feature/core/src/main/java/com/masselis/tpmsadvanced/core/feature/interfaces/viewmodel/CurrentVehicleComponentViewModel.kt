package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import javax.inject.Inject

internal class CurrentVehicleComponentViewModel @Inject constructor(
    currentVehicleUseCase: CurrentVehicleUseCase
) : ViewModel() {
    val stateFlow = currentVehicleUseCase.flow
}
