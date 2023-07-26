package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CompletableDeferred
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
        val deferred = CompletableDeferred<Unit>()
        GlobalScope.launch(NonCancellable) {
            try {
                database.selectAll()
                    .firstOrNull { it.uuid != vehicle.uuid }
                    ?.also { currentVehicleUseCase.setAsCurrent(it) }
                    ?: error("Cannot delete the last vehicle in the database")
                scope.cancel()
                database.setIsDeleting(vehicle.uuid)
                deferred.complete(Unit)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                deferred.completeExceptionally(e)
                throw e
            }
            delay(1.seconds)
            database.delete(vehicle.uuid)
        }
        deferred.await()
    }
}
