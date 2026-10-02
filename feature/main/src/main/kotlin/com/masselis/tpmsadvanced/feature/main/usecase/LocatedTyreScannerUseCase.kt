package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class LocatedTyreScannerUseCase(
    private val source: BluetoothLeScanner,
    private val currentLocation: Location,
    private val sensorBindingUseCase: SensorBindingUseCase,
) {
    fun highDutyScan(): Flow<Tyre.Located> = source.highDutyScan().mapWithLocation()

    fun normalScan(): Flow<Tyre.Located> = source.normalScan().mapWithLocation()

    @Suppress("CyclomaticComplexMethod")
    private fun Flow<ScannerRecord>.mapWithLocation() = this
        .mapNotNull { it as? Tyre }
        .mapNotNull { tyreRecord ->
            // Takes the location from the bound sensor if any exists
            sensorBindingUseCase.boundSensor()
                .value
                .takeIf { tyreRecord.sensorId == it?.id }
                ?.let { sensor -> Tyre.Located(tyreRecord, sensor.location) }
            // Cannot compute location from a bound sensor, let's take a look at the tyreRecord itself
            // to see if it matches the current location
                ?: when (tyreRecord) {
                    is Tyre.Located ->
                        when (currentLocation) {
                            is Location.Wheel ->
                                tyreRecord.location

                            is Location.Axle -> when (tyreRecord.location) {
                                is Location.Wheel -> (tyreRecord.location as Location.Wheel).toAxle()
                                is Location.Axle -> tyreRecord.location
                                is Location.Side -> null
                            }

                            is Location.Side -> when (tyreRecord.location) {
                                is Location.Wheel -> (tyreRecord.location as Location.Wheel).toSide()
                                is Location.Axle -> null
                                is Location.Side -> tyreRecord.location
                            }
                        }

                    is Tyre.Unlocated -> null
                }.let { locationToCompare ->
                    if (locationToCompare == currentLocation)
                        Tyre.Located(tyreRecord, currentLocation)
                    else
                        null
                }
        }
}
