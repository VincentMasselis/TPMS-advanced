package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

internal class ListenTyreSmartDutyUseCase(
    private val scanner: BluetoothLeScanner,
) : ListenTyreUseCase {
    override fun listen() = flow {
        emit(scanner.highDutyScan().first())
        emitAll(scanner.normalScan())
    }
}
