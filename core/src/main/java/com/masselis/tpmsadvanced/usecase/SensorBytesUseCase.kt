package com.masselis.tpmsadvanced.usecase

import androidx.core.util.size
import com.masselis.tpmsadvanced.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.ioc.SingleInstance
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@SingleInstance
class SensorBytesUseCase @Inject constructor(
    private val location: TyreLocation,
    scanner: BluetoothLeScanner
) {
    private val sharedFlow = scanner
        .scanFlow
        .mapNotNull { result -> result.scanRecord?.manufacturerSpecificData?.takeIf { it.size > 0 } }
        .map { it.valueAt(0) }
        .filter { it[2].toUByte() == location.byte }
        .shareIn(
            CoroutineScope(EmptyCoroutineContext),
            SharingStarted.WhileSubscribed(),
            1
        )

    fun listen() = sharedFlow
}