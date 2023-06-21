package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

internal class DeleteVehicleUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val database: VehicleDatabase,
    private val scope: CoroutineScope,
) {

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun delete() {
        currentVehicleUseCase.setAsCurrent(
            database.selectAll().firstOrNull { it.uuid != vehicle.uuid }
                ?: error("Cannot delete the last vehicle in the database")
        )
        GlobalScope.launch(NonCancellable) {
            scope.cancel()
            database.prepareDelete(vehicle.uuid)
            delay(1.seconds)
            database.delete(vehicle.uuid)
        }
    }
}
