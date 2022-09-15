package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.record.model.Tyre
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@TyreScope
public class TyreUseCaseImpl @Inject internal constructor(
    private val location: TyreLocation,
    private val scanner: BluetoothLeScanner
) : TyreUseCase {
    override fun listen(): SharedFlow<Tyre> = flow {
        emit(scanner.highDutyScan().filterLocation().first())
        emitAll(scanner.normalScan().filterLocation())
    }.shareIn(
        CoroutineScope(EmptyCoroutineContext),
        SharingStarted.WhileSubscribed(
            stopTimeout = 20.seconds,
            replayExpiration = 20.seconds
        ),
        1
    )

    private fun Flow<Tyre>.filterLocation() = filter { it.location == location }
}
