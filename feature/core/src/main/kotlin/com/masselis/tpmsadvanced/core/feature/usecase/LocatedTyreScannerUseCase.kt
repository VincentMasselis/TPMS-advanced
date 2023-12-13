package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class LocatedTyreScannerUseCase @Inject constructor(
    private val source: BluetoothLeScanner,
    private val currentLocation: Location,
    private val sensorBindingUseCase: SensorBindingUseCase,
) {
    fun highDutyScan(): Flow<Tyre.Located> = source.highDutyScan().mapWithLocation()

    fun normalScan(): Flow<Tyre.Located> = source.normalScan().mapWithLocation()

    private fun Flow<Tyre.SensorInput>.mapWithLocation() = this
        .mapNotNull { tyre ->
            // Takes the location from the bound sensor if any exists
            sensorBindingUseCase.boundSensor()
                .takeIf { tyre.id == it?.id }
                ?.let { sensor -> Tyre.Located(tyre, sensor.location) }
            // Cannot compute location from a bound sensor, let's take a look at the tyre itself
                ?: when (tyre) {
                    is Tyre.SensorLocated -> when (currentLocation) {
                        is Location.Axle -> currentLocation.axle == tyre.location.axle
                        is Location.Side -> currentLocation.side == tyre.location.side
                        is Location.Wheel -> currentLocation.location == tyre.location
                    }.let { compatibleSensorLocation ->
                        if (compatibleSensorLocation) Tyre.Located(tyre, currentLocation)
                        else null
                    }

                    is Tyre.Unlocated -> null
                }
        }
}