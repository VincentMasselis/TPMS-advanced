package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
public class VehicleListUseCase @Inject internal constructor(
    database: VehicleDatabase
) {
    public val vehicleListFlow: Flow<List<Vehicle>> = database.selectAllFlow()
        .map { list -> list.sortedBy { it.uuid } }
        .distinctUntilChanged()
}
