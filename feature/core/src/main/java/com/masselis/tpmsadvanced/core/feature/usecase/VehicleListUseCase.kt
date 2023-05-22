package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import dagger.Reusable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
internal class VehicleListUseCase @Inject constructor(
    database: VehicleDatabase
) {
    val vehicleListFlow = database.selectAllFlow()
        .map { list -> list.sortedBy { it.uuid } }
        .distinctUntilChanged()
}
