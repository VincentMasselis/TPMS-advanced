package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

internal class DeleteVehicleUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val database: VehicleDatabase,
) {

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun delete() {
        database.setIsCurrent(database.selectAll().first { it.uuid != vehicle.uuid }.uuid, true)
        GlobalScope.launch {
            delay(1.seconds)
            database.delete(vehicle.uuid)
        }
    }
}
