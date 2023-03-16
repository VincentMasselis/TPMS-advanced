package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import kotlinx.coroutines.flow.StateFlow

internal interface CurrentVehicleComponentViewModel {
    val stateFlow : StateFlow<VehicleComponent>
}