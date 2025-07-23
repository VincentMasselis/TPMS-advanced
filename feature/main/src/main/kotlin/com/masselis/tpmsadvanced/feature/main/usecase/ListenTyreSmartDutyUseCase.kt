package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

internal class ListenTyreSmartDutyUseCase(
    private val scanner: LocatedTyreScannerUseCase,
) : ListenTyreUseCase {
    override fun listen(): Flow<Tyre.Located> = flow {
        emit(scanner.highDutyScan().first())
        emitAll(scanner.normalScan())
    }
}
