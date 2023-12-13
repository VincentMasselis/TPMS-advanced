package com.masselis.tpmsadvanced.pecham_binding.usecase

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class ListTyreUseCase @Inject constructor(
    private val scanner: BluetoothLeScanner,
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
) : () -> Flow<Pair<List<ListTyreUseCase.Available.ReadyToBind>, List<ListTyreUseCase.Available.Bound>>> {
    override fun invoke(): Flow<Pair<List<Available.ReadyToBind>, List<Available.Bound>>> = scanner
        .highDutyScan()
        .mapNotNull {
            when (it) {
                is Tyre.SensorLocated -> null // Only interested by unlocated sensors
                is Tyre.Unlocated -> it
            }
        }
        .flatMapLatest { foundTyre ->
            sensorDatabase
                .selectByIdFlow(foundTyre.id)
                .flatMapLatest { foundSensor ->
                    if (foundSensor == null) flowOf(Available.ReadyToBind(foundTyre))
                    else vehicleDatabase.selectBySensorId(foundSensor.id)
                        .map { Available.Bound(foundTyre, foundSensor, it!!) }
                }
        }
        .scan(mutableListOf<Available>()) { acc, available ->
            if (acc.none { it.tyre.id == available.tyre.id })
                acc += available
            acc.sortByDescending { it.tyre.rssi }
            acc
        }
        .map { availables ->
            availables
                .partition {
                    when (it) {
                        is Available.ReadyToBind -> true
                        is Available.Bound -> false
                    }
                }
                .let { (unbound, bound) ->
                    Pair(
                        unbound.map { it as Available.ReadyToBind },
                        bound.map { it as Available.Bound }
                    )
                }
        }
        .distinctUntilChanged()
        .flowOn(Default)


    sealed interface Available : Parcelable {

        val tyre: Tyre

        @Parcelize
        @JvmInline
        value class ReadyToBind(override val tyre: Tyre.Unlocated) : Available

        @Parcelize
        data class Bound(
            override val tyre: Tyre.Unlocated,
            val sensor: Sensor,
            val vehicle: Vehicle
        ) : Available
    }
}
