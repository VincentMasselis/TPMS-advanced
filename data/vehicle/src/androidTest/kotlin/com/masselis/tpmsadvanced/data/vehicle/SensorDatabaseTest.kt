package com.masselis.tpmsadvanced.data.vehicle

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.ioc.InternalComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
internal class SensorDatabaseTest {

    private lateinit var database: Database
    private lateinit var currentVehicleUuid: UUID
    private lateinit var vehicleQueries: VehicleQueries
    private lateinit var sensorQueries: SensorQueries
    private lateinit var sensorDatabase: SensorDatabase

    @Before
    fun setup() {
        appContext.getDatabasePath("car.db").delete()
        val debugComponent = InternalComponent.debugComponentFactory.build()
        database = debugComponent.database
        vehicleQueries = database.vehicleQueries
        sensorQueries = database.sensorQueries
        currentVehicleUuid = vehicleQueries.currentFavourite().executeAsOne().uuid
        sensorDatabase = SensorDatabase(database)
    }

    private fun assertSensorId(id: Int, vehicleUuid: UUID, vararg location: SensorLocation) =
        assertEquals(
            id,
            sensorQueries
                .selectByVehicleAndLocation(vehicleUuid, location.toList())
                .executeAsOne()
                .id
        )

    private fun assertSensorCount(count: Long, vehicleUuid: UUID) =
        assertEquals(count, sensorQueries.countByVehicle(vehicleUuid).executeAsOne())

    @Test
    fun upsert() = runTest {
        assertSensorCount(0, currentVehicleUuid)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), currentVehicleUuid)
        assertSensorId(1, currentVehicleUuid, FRONT_LEFT)
        assertSensorCount(1, currentVehicleUuid)
    }

    @Test
    fun upsertSensorToANewCar() = runTest {
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), currentVehicleUuid)
        assertSensorCount(1, currentVehicleUuid)

        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.CAR, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), uuid)
        assertSensorCount(0, currentVehicleUuid)
        assertSensorId(1, uuid, FRONT_LEFT)
        assertSensorCount(1, uuid)
    }

    @Test
    fun upsertNewSensorAtTheSameLocation() = runTest {
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), currentVehicleUuid)
        assertSensorCount(1, currentVehicleUuid)

        sensorDatabase.upsert(Sensor(2, FRONT_LEFT), currentVehicleUuid)
        assertSensorId(2, currentVehicleUuid, FRONT_LEFT)
        assertSensorCount(1, currentVehicleUuid)
    }

    @Test
    fun upsertNewSensorForSingleAxleTrailer() = runTest {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.SINGLE_AXLE_TRAILER, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), uuid)
        sensorDatabase.upsert(Sensor(2, REAR_LEFT), uuid)
        assertSensorCount(1, uuid)
        assertSensorId(2, uuid, FRONT_LEFT, REAR_LEFT)

        sensorDatabase.upsert(Sensor(3, FRONT_RIGHT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(3, uuid, FRONT_RIGHT, REAR_RIGHT)

        sensorDatabase.upsert(Sensor(4, REAR_RIGHT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(4, uuid, FRONT_RIGHT, REAR_RIGHT)
    }

    @Test
    fun upsertNewSensorForMotorcycle() = runTest {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.MOTORCYCLE, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), uuid)
        sensorDatabase.upsert(Sensor(2, FRONT_RIGHT), uuid)
        assertSensorCount(1, uuid)
        assertSensorId(2, uuid, FRONT_LEFT, FRONT_RIGHT)

        sensorDatabase.upsert(Sensor(3, REAR_LEFT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(3, uuid, REAR_LEFT, REAR_RIGHT)

        sensorDatabase.upsert(Sensor(4, REAR_RIGHT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(4, uuid, REAR_LEFT, REAR_RIGHT)
    }

    @Test
    fun upsertNewSensorForTadpoleThreeWheeler() = runTest {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.TADPOLE_THREE_WHEELER, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), uuid)
        sensorDatabase.upsert(Sensor(2, FRONT_RIGHT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(1, uuid, FRONT_LEFT)
        assertSensorId(2, uuid, FRONT_RIGHT)

        sensorDatabase.upsert(Sensor(3, REAR_LEFT), uuid)
        assertSensorCount(3, uuid)
        assertSensorId(3, uuid, REAR_RIGHT, REAR_LEFT)

        sensorDatabase.upsert(Sensor(4, REAR_RIGHT), uuid)
        assertSensorCount(3, uuid)
        assertSensorId(4, uuid, REAR_RIGHT, REAR_LEFT)
    }

    @Test
    fun upsertNewSensorForDeltaThreeWheeler() = runTest {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.DELTA_THREE_WHEELER, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, FRONT_LEFT), uuid)
        sensorDatabase.upsert(Sensor(2, FRONT_RIGHT), uuid)
        assertSensorCount(1, uuid)
        assertSensorId(2, uuid, FRONT_RIGHT, FRONT_LEFT)

        sensorDatabase.upsert(Sensor(3, REAR_LEFT), uuid)
        assertSensorCount(2, uuid)
        assertSensorId(3, uuid, REAR_LEFT)

        sensorDatabase.upsert(Sensor(4, REAR_RIGHT), uuid)
        assertSensorCount(3, uuid)
        assertSensorId(4, uuid, REAR_RIGHT)
    }
}
