package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class VehicleUseCase @Inject constructor(
    private val vehicleId: UUID,
    private val database: VehicleDatabase
) {
    fun vehicleFlow() = database.selectByUuid(vehicleId).distinctUntilChanged()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun delete() {
        database.setIsCurrent(database.selectAll().first { it.uuid != vehicleId }.uuid, true)
        GlobalScope.launch {
            delay(1.seconds)
            database.delete(vehicleId)
        }
    }
}
