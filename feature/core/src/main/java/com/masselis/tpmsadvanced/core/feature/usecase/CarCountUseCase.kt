package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CarCountUseCase @Inject constructor(
    private val carDatabase: CarDatabase
) {
    fun count() = carDatabase.selectAllFlow()
        .map { it.size }
        .distinctUntilChanged()
}
