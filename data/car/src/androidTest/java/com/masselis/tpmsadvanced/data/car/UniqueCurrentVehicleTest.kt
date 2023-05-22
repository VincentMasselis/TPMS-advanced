package com.masselis.tpmsadvanced.data.car

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.masselis.tpmsadvanced.data.car.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class UniqueCurrentVehicleTest {

    private lateinit var database: Database
    private lateinit var vehicleQueries: VehicleQueries
    private lateinit var debugVehicleQueries: DebugVehicleQueries

    @Before
    fun setup() {
        val debugComponent = DataVehicleComponent.debugComponentFactory.build()
        database = debugComponent.database
        vehicleQueries = database.vehicleQueries
        debugVehicleQueries = database.debugVehicleQueries
        vehicleQueries.currentFavourite()
            .asFlow()
            .mapToList(IO)
            .onEach { assertEquals(1, it.size) }
            .launchIn(GlobalScope)
    }

    private fun carList() = debugVehicleQueries.carList().executeAsList()

    @Test
    fun default() {
        assertEquals(1, carList().size)
        assertTrue { carList().first().isFavourite }
    }

    @Test
    fun insert1() {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "test", false)
        assertTrue { carList().first { it.uuid == uuid }.isFavourite.not() }
    }

    @Test
    fun insert2() {
        vehicleQueries.insert(UUID.randomUUID(), Vehicle.Kind.CAR, "test", true)
    }

    @Test
    fun insert3() {
        vehicleQueries.insert(UUID.randomUUID(), Vehicle.Kind.CAR, "test", false)
        vehicleQueries.insert(UUID.randomUUID(), Vehicle.Kind.CAR, "test", true)
        assertEquals(3, carList().size)
    }

    @Test
    fun insert4() {
        vehicleQueries.insert(UUID.randomUUID(), Vehicle.Kind.CAR, "test", true)
        vehicleQueries.insert(UUID.randomUUID(), Vehicle.Kind.CAR, "test", false)
        assertEquals(3, carList().size)
    }

    @Test
    fun update1() {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "TEST", false)
        vehicleQueries.setAsFavourite(true, uuid)
        assertTrue { carList().first { it.uuid == uuid }.isFavourite }
    }

    @Test
    fun update2() {
        vehicleQueries.setAsFavourite(true, carList().first().uuid)
    }

    @Test
    fun update3() {
        vehicleQueries.setAsFavourite(false, carList().first().uuid)
    }

    @Test
    fun delete1() {
        vehicleQueries.delete(carList().first().uuid)
    }

    @Test
    fun delete2() {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "TEST", true)
        vehicleQueries.delete(uuid)
    }

    @Test
    fun delete3() {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "TEST", true)
        vehicleQueries.delete(uuid)
        carList().forEach { vehicleQueries.delete(it.uuid) }
    }

    @Test
    fun delete4() {
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "TEST", false)
        vehicleQueries.delete(uuid)
        carList().forEach { vehicleQueries.delete(it.uuid) }
    }

    @Test
    fun combinedTest() {
        assertUniqueCurrent()
        vehicleQueries.delete(carList().first().uuid)
        assertUniqueCurrent()
        vehicleQueries.setAsFavourite(false, carList().first().uuid)
        assertUniqueCurrent()
        val uuid = UUID.randomUUID()
        vehicleQueries.insert(uuid, Vehicle.Kind.CAR, "TEST", false)
        assertEquals(2, carList().size)
        assertUniqueCurrent()
        vehicleQueries.setAsFavourite(true, uuid)
        assertUniqueCurrent()
        vehicleQueries.delete(carList().first { it.uuid != uuid }.uuid)
        assertEquals(1, carList().size)
        assertUniqueCurrent()
        vehicleQueries.delete(carList().first().uuid)
        assertUniqueCurrent()
    }

    @After
    fun assertUniqueCurrent() {
        assertEquals(
            1,
            debugVehicleQueries.favouriteCount().executeAsOne(),
            "Vehicle list must contains exactly 1 favourite. Vehicle list: ${carList()}"
        )
    }
}
