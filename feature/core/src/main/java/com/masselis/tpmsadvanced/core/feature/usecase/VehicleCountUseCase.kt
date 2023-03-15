package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class VehicleCountUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase
) {
    fun count() = vehicleDatabase.selectAllFlow()
        .map { it.size }
        .distinctUntilChanged()
}
