package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ListenTyreSmartDutyUseCase @Inject constructor(
    private val scanner: LocatedTyreScannerUseCase,
) : ListenTyreUseCase {
    override fun listen(): Flow<Tyre.Located> = flow {
        emit(scanner.highDutyScan().first())
        emitAll(scanner.normalScan())
    }
}
