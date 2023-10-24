package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.dematerializeCompletion
import com.masselis.tpmsadvanced.core.common.materializeCompletion
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@TyreComponent.Scope
public class TyreUseCaseImpl @Inject internal constructor(
    @Named("base") private val vehicle: Vehicle,
    private val locations: Set<SensorLocation>,
    private val tyreDatabase: TyreDatabase,
    private val scanner: BluetoothLeScanner,
    scope: CoroutineScope,
) : TyreUseCase {

    private val flow = flow {
        emit(scanner.highDutyScan().filterLocation().first())
        emitAll(scanner.normalScan().filterLocation())
    }
        // A real sysgration sensor emits the same value up to 10 times, to avoid to save the same
        // value 10 times, I prefer to use this distinct operator. Unfortunately, even if the value
        // is the same, the timestamp could differ a little so I always set this value to 0 to avoid
        // perturbations when using `distinctUntilChangedBy`.
        .distinctUntilChangedBy { it.copy(timestamp = 0.0) }
        .onEach { tyre -> tyreDatabase.insert(tyre, vehicle.uuid) }
        .materializeCompletion()
        .shareIn(scope, WhileSubscribed())
        .dematerializeCompletion()
        .onStart {
            tyreDatabase.latestByTyreLocationByVehicle(locations, vehicle.uuid)
                ?.also { emit(it) }
        }

    override fun listen(): Flow<Tyre> = flow

    private fun Flow<Tyre>.filterLocation() = filter { locations.contains(it.location) }
}
