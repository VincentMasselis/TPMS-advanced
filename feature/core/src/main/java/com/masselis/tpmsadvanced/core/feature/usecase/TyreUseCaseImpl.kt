package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.car.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
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
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@TyreScope
public class TyreUseCaseImpl @Inject internal constructor(
    @Named("base") private val vehicle: Vehicle,
    private val locations: Set<SensorLocation>,
    private val tyreDatabase: TyreDatabase,
    private val scanner: BluetoothLeScanner,
    scope: CoroutineScope
) : TyreUseCase {

    private val flow = flow {
        emit(scanner.highDutyScan().filterLocation().first())
        emitAll(scanner.normalScan().filterLocation())
    }
        // When a sensor needs to send a value, it emit the same value 10 times within a 1 second
        // frame. To avoid saving the same value multiple times into the database, a debounce is
        // applied to fetch only the latest value.
        .debounce(1.seconds)
        .onEach { tyre -> tyreDatabase.insert(tyre, vehicle.uuid) }
        .shareIn(
            scope,
            SharingStarted.WhileSubscribed()
        )
        .onStart {
            tyreDatabase.latestByTyreLocationByVehicle(locations, vehicle.uuid)
                ?.also { emit(it) }
        }

    override fun listen(): Flow<Tyre> = flow

    private fun Flow<Tyre>.filterLocation() = filter { locations.contains(it.location) }
}
