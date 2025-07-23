package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

public class VehicleListUseCase internal constructor(
    database: VehicleDatabase
) {
    public val vehicleListFlow: Flow<List<Vehicle>> = database.selectAll()
        .asFlow()
        .map { list -> list.sortedBy { it.uuid } }
        .distinctUntilChanged()
}
