package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.core.common.dematerializeCompletion
import com.masselis.tpmsadvanced.core.common.materializeCompletion
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

internal interface ListenTyreWithDatabaseUseCase : ListenTyreUseCase {
    class Impl(
        vehicle: Vehicle,
        location: Location,
        tyreDatabase: TyreDatabase,
        listenTyreUseCase: ListenTyreUseCase,
        scope: CoroutineScope,
    ) : ListenTyreWithDatabaseUseCase {

        private val flow = listenTyreUseCase
            .listen()
            .onEach { tyre -> tyreDatabase.insert(tyre, vehicle.uuid) }
            .materializeCompletion()
            .shareIn(scope, WhileSubscribed())
            .dematerializeCompletion()
            .onStart {
                tyreDatabase
                    .latestByTyreLocationByVehicle(location, vehicle.uuid)
                    .execute()
                    ?.also { emit(it) }
            }
            .flowOn(Dispatchers.IO)

        override fun listen(): Flow<Tyre.Located> = flow
    }

    class Wrapper(private val source: ListenTyreUseCase) : ListenTyreWithDatabaseUseCase {
        override fun listen(): Flow<Tyre.Located> = source.listen()
    }
}
