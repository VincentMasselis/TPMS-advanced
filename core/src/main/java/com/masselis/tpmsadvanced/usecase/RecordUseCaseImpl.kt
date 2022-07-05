package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.ioc.SingleInstance
import com.masselis.tpmsadvanced.model.Record
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@SingleInstance
class RecordUseCaseImpl @Inject constructor(
    private val location: TyreLocation,
    private val scanner: BleScanUseCase
) : RecordUseCase {
    override fun listen() = flow {
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

    private fun Flow<Record>.filterLocation() = filter { it.location() == location.byte }
}