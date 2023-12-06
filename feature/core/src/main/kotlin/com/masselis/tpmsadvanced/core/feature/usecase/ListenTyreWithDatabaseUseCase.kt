package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.dematerializeCompletion
import com.masselis.tpmsadvanced.core.common.materializeCompletion
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

internal class ListenTyreWithDatabaseUseCase(
    private val vehicle: Vehicle,
    private val locations: Set<SensorLocation>,
    private val tyreDatabase: TyreDatabase,
    listenTyreUseCase: ListenTyreUseCase,
    scope: CoroutineScope,
) : ListenTyreUseCase {

    private val flow = listenTyreUseCase
        .listen()
        .onEach { tyre -> tyreDatabase.insert(tyre, vehicle.uuid) }
        .materializeCompletion()
        .shareIn(scope, WhileSubscribed())
        .dematerializeCompletion()
        .onStart {
            tyreDatabase.latestByTyreLocationByVehicle(locations, vehicle.uuid)
                ?.also { emit(it) }
        }
        .flowOn(Dispatchers.IO)

    override fun listen(): Flow<Tyre> = flow
}
