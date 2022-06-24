package com.masselis.tpmsadvanced.usecase

import android.bluetooth.le.ScanResult
import androidx.core.util.size
import com.masselis.tpmsadvanced.ioc.SingleInstance
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@SingleInstance
class SensorByteArrayUseCase @Inject constructor(
    private val location: TyreLocation,
    private val scanner: BleScanUseCase
) {
    fun listen(): Flow<ByteArray> = flow {
        emit(scanner.highDutyScan().sensorBytes().first())
        emitAll(scanner.normalScan().sensorBytes())
    }.shareIn(
        CoroutineScope(EmptyCoroutineContext),
        SharingStarted.WhileSubscribed(
            stopTimeout = 20.seconds,
            replayExpiration = 20.seconds
        ),
        1
    )

    private fun Flow<ScanResult>.sensorBytes() = this
        .mapNotNull { result -> result.scanRecord?.manufacturerSpecificData?.takeIf { it.size > 0 } }
        .map { it.valueAt(0) }
        .filter { it[0].toUByte() == location.byte }
}