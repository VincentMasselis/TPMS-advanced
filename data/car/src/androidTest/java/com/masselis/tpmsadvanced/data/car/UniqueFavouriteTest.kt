package com.masselis.tpmsadvanced.data.car

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.car.ioc.DebugComponent
import com.masselis.tpmsadvanced.data.car.ioc.dataCarComponent
import kotlinx.coroutines.CoroutineScope
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
internal class UniqueFavouriteTest {

    private lateinit var debugComponent: DebugComponent
    private lateinit var database: Database
    private lateinit var carQueries: CarQueries
    private lateinit var debugCarQueries: DebugCarQueries

    @Before
    fun setup() {
        appContext.getDatabasePath("car.db").delete()
        debugComponent = dataCarComponent.debugFactory.build()
        database = debugComponent.database
        carQueries = database.carQueries
        debugCarQueries = database.debugCarQueries
        carQueries.currentFavourite()
            .asFlow()
            .mapToList(IO)
            .onEach { assertEquals(1, it.size) }
            .launchIn(GlobalScope)
    }

    private fun carList() = debugCarQueries.carList().executeAsList()

    @Test
    fun default() {
        assertEquals(1, carList().size)
        assertTrue { carList().first().isFavourite }
    }

    @Test
    fun insert1() {
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "test", false)
        assertTrue { carList().first { it.uuid == uuid }.isFavourite.not() }
    }

    @Test
    fun insert2() {
        carQueries.insert(UUID.randomUUID(), "test", true)
    }

    @Test
    fun insert3() {
        carQueries.insert(UUID.randomUUID(), "test", false)
        carQueries.insert(UUID.randomUUID(), "test", true)
        assertEquals(3, carList().size)
    }

    @Test
    fun insert4() {
        carQueries.insert(UUID.randomUUID(), "test", true)
        carQueries.insert(UUID.randomUUID(), "test", false)
        assertEquals(3, carList().size)
    }

    @Test
    fun update1() {
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "TEST", false)
        carQueries.setAsFavourite(true, uuid)
        assertTrue { carList().first { it.uuid == uuid }.isFavourite }
    }

    @Test
    fun update2() {
        carQueries.setAsFavourite(true, carList().first().uuid)
    }

    @Test
    fun update3() {
        carQueries.setAsFavourite(false, carList().first().uuid)
    }

    @Test
    fun delete1() {
        carQueries.delete(carList().first().uuid)
    }

    @Test
    fun delete2() {
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "TEST", true)
        carQueries.delete(uuid)
    }

    @Test
    fun delete3() {
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "TEST", true)
        carQueries.delete(uuid)
        carList().forEach { carQueries.delete(it.uuid) }
    }

    @Test
    fun delete4() {
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "TEST", false)
        carQueries.delete(uuid)
        carList().forEach { carQueries.delete(it.uuid) }
    }

    @Test
    fun combinedTest() {
        assertUniqueFavourite()
        carQueries.delete(carList().first().uuid)
        assertUniqueFavourite()
        carQueries.setAsFavourite(false, carList().first().uuid)
        assertUniqueFavourite()
        val uuid = UUID.randomUUID()
        carQueries.insert(uuid, "TEST", false)
        assertEquals(2, carList().size)
        assertUniqueFavourite()
        carQueries.setAsFavourite(true, uuid)
        assertUniqueFavourite()
        carQueries.delete(carList().first { it.uuid != uuid }.uuid)
        assertEquals(1, carList().size)
        assertUniqueFavourite()
        carQueries.delete(carList().first().uuid)
        assertUniqueFavourite()
    }

    @After
    fun assertUniqueFavourite() {
        assertEquals(
            1,
            debugCarQueries.favouriteCount().executeAsOne(),
            "Car list must contains exactly 1 favourite. Car list: ${carList()}"
        )
    }
}