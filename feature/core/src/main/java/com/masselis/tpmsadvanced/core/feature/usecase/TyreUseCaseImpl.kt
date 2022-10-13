package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val sensorDatabase: SensorDatabase,
    private val tyreDatabase: TyreDatabase,
    private val scanner: BluetoothLeScanner,
    private val scope: CoroutineScope
) : TyreUseCase {
    override fun listen(): SharedFlow<Tyre> = flow {
        emit(scanner.highDutyScan().filterLocation().first())
        emitAll(scanner.normalScan().filterLocation())
    }.onEach {
        val sensor = sensorDatabase.selectByCarAndLocationFlow(carId, location).first()
            ?: return@onEach
        tyreDatabase.insert(it, sensor.id)
    }.shareIn(
        scope,
        SharingStarted.WhileSubscribed(
            stopTimeout = 20.seconds,
            replayExpiration = 20.seconds
        ),
        1
    )

    private fun Flow<Tyre>.filterLocation() = filter { it.location == location }
}
