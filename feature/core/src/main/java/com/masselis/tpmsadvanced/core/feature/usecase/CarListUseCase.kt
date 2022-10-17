package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import dagger.Reusable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
internal class CarListUseCase @Inject constructor(
    database: CarDatabase
) {
    val carListFlow = database.selectAllFlow()
        .map { list -> list.sortedBy { it.uuid } }
        .distinctUntilChanged()
}
