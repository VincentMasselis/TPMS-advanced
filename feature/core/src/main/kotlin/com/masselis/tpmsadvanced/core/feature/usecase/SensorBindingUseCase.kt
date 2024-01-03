package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.database.asOneOrNullFlow
import com.masselis.tpmsadvanced.core.database.asOneOrNullStateFlow
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@TyreComponent.Scope
internal class SensorBindingUseCase @Inject constructor(
    @Named("base") private val currentVehicle: Vehicle,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
    currentLocation: Location,
    scope: CoroutineScope,
) {

    private val boundSensor = sensorDatabase.selectByVehicleAndLocation(
        currentVehicle.uuid,
        currentLocation
    ).asOneOrNullStateFlow(scope, WhileSubscribed())

    fun boundSensor(): StateFlow<Sensor?> = boundSensor

    fun boundVehicle(sensor: Sensor) = vehicleDatabase.selectBySensorId(sensor.id).asOneOrNullFlow()

    suspend fun bind(sensor: Sensor) = sensorDatabase.upsert(sensor, currentVehicle.uuid)
}
