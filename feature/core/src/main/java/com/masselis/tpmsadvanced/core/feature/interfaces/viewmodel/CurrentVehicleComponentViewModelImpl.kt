package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import javax.inject.Inject

internal class CurrentVehicleComponentViewModelImpl @Inject constructor(
    currentVehicleUseCase: CurrentVehicleUseCase
) : ViewModel(), CurrentVehicleComponentViewModel {
    override val stateFlow = currentVehicleUseCase.flow
}
