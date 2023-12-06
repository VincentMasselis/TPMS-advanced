package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LocationFilteredScannerUseCase(
    private val source: BluetoothLeScanner,
    private val locations: Set<SensorLocation>,
    private val sensorBindingUseCase: SensorBindingUseCase,
) : BluetoothLeScanner by source {

    override fun highDutyScan(): Flow<Tyre> = source.highDutyScan().filterByLocation()

    override fun normalScan(): Flow<Tyre> = source.normalScan().filterByLocation()

    private fun Flow<Tyre>.filterByLocation() = this
        .map { tyre ->
            if (tyre.location == null)
                sensorBindingUseCase.boundSensor()
                    .takeIf { tyre.id == it?.id }
                    ?.let { sensor -> tyre.copy(location = sensor.location) }
                    ?: tyre
            else
                tyre
        }
        .mapNotNull { tyre -> tyre.takeIf { locations.contains(it.location) } }
}