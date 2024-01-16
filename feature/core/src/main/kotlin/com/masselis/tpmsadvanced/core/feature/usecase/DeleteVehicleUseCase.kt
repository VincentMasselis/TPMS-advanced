package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

internal class DeleteVehicleUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val database: VehicleDatabase,
    private val scope: CoroutineScope,
) {

    suspend fun delete() = withContext(NonCancellable + IO) {
        // Cancel the current vehicle
        scope.cancel()
        // Set one of the available vehicle has the current vehicle. Doing this trigger compose
        // methods and reload the screens to the main screen where the vehicle tyres are displayed.
        database.selectAll()
            .execute()
            .firstOrNull { it.uuid != vehicle.uuid }
            ?.also { currentVehicleUseCase.setAsCurrent(it) }
            ?: error("Cannot delete the last vehicle in the database")
        // Flags the vehicle to be deleted
        database.setIsDeleting(vehicle.uuid)
        launch {
            // Deletes the vehicle after a 1 second timeout
            delay(1.seconds)
            database.delete(vehicle.uuid)
        }
    }
}
