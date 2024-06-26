package com.masselis.tpmsadvanced.data.vehicle

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.ioc.DebugComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        database = DebugComponent.database
        vehicleQueries = database.vehicleQueries
        sensorQueries = database.sensorQueries
        currentVehicleUuid = vehicleQueries.currentFavourite().executeAsOne().uuid
        sensorDatabase = SensorDatabase(database)
    }

    private fun assertSensorId(id: Int, vehicleUuid: UUID, location: Location) =
        assertEquals(
            id,
            sensorQueries
                .selectByVehicleAndLocation(vehicleUuid, location)
                .executeAsOne()
                .id
        )

    private fun assertSensorCount(count: Long, vehicleUuid: UUID) =
        assertEquals(count, sensorQueries.countByVehicle(vehicleUuid).executeAsOne())

    @Test
    fun simpleInsert() = runTest {
        assertSensorCount(0, currentVehicleUuid)
        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_LEFT)), currentVehicleUuid)
        assertSensorId(1, currentVehicleUuid, Location.Wheel(FRONT_LEFT))
        assertSensorCount(1, currentVehicleUuid)
    }

    @Test
    fun insertToACarThanUpsertToAnOtherCar() = runTest {
        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_LEFT)), currentVehicleUuid)
        assertSensorCount(1, currentVehicleUuid)

        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Kind.CAR, "MOCK", false)
        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_LEFT)), uuid)
        assertSensorCount(0, currentVehicleUuid)
        assertSensorId(1, uuid, Location.Wheel(FRONT_LEFT))
        assertSensorCount(1, uuid)
    }

    @Test
    fun insertSensor1ThenInsertAtTheSamePlaceSensor2() = runTest {
        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_LEFT)), currentVehicleUuid)
        assertSensorCount(1, currentVehicleUuid)

        sensorDatabase.upsert(Sensor(2, Location.Wheel(FRONT_LEFT)), currentVehicleUuid)
        assertSensorId(2, currentVehicleUuid, Location.Wheel(FRONT_LEFT))
        assertSensorCount(1, currentVehicleUuid)
    }

    @Test
    fun insertSensor1ThenUpsertWithANewLocation() = runTest {
        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_LEFT)), currentVehicleUuid)
        assertSensorCount(1, currentVehicleUuid)

        sensorDatabase.upsert(Sensor(1, Location.Wheel(FRONT_RIGHT)), currentVehicleUuid)
        assertSensorId(1, currentVehicleUuid, Location.Wheel(FRONT_RIGHT))
        assertSensorCount(1, currentVehicleUuid)
    }

    @Test
    fun upsertSensorToAWrongLocationForTheKind() = runTest {
        assertSensorCount(0, currentVehicleUuid)
        assertFailsWith<IllegalArgumentException> {
            sensorDatabase.upsert(Sensor(1, Location.Side(LEFT)), currentVehicleUuid)
        }
        assertSensorCount(0, currentVehicleUuid)
    }
}
