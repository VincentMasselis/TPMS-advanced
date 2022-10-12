package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*
import javax.inject.Inject

internal class CarUseCase @Inject constructor(
    carId: UUID,
    database: CarDatabase
) {
    val flow = database.selectByUuid(carId).distinctUntilChanged()
}