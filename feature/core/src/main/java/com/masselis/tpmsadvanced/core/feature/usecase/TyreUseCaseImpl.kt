package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.car.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@TyreScope
public class TyreUseCaseImpl @Inject internal constructor(
    private val carId: UUID,
    private val location: SensorLocation,
    private val tyreDatabase: TyreDatabase,
    private val scanner: BluetoothLeScanner,
    scope: CoroutineScope
) : TyreUseCase {

    private val flow = flow {
        emit(scanner.highDutyScan().filterLocation().first())
        emitAll(scanner.normalScan().filterLocation())
    }.onEach { tyre -> tyreDatabase.insert(tyre, carId) }
        .shareIn(
            scope,
            SharingStarted.WhileSubscribed(
                stopTimeout = 20.seconds,
                replayExpiration = 20.seconds
            ),
            1
        )

    override fun listen(): SharedFlow<Tyre> = flow

    private fun Flow<Tyre>.filterLocation() = filter { it.location == location }
}
