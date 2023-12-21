package com.masselis.tpmsadvanced.core

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.feature.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class ListenTyreWithDatabaseUseCaseTest {

    private lateinit var vehicle: Vehicle
    private lateinit var location: Location
    private lateinit var tyreDatabase: TyreDatabase
    private lateinit var listenTyreUseCase: ListenTyreUseCase

    private fun CoroutineScope.test() = ListenTyreWithDatabaseUseCase(
        vehicle,
        location,
        tyreDatabase,
        listenTyreUseCase,
        this
    )

    @Before
    fun setup() {
        vehicle = mockk {
            val uuid = UUID.randomUUID()
            every { this@mockk.uuid } returns uuid
        }
        location = Location.Wheel(FRONT_LEFT)
        tyreDatabase = mockk {
            coEvery { insert(any(), any()) } returns Unit
            every { latestByTyreLocationByVehicle(any(), any()) } returns null
        }
        listenTyreUseCase = mockk {
            every { listen() } returns emptyFlow()
        }
    }

    @Test
    fun `2 tyres emit with same id at front left`() = runTest {
        val tyresToEmit = listOf(
            Tyre.Located(now(), -20, 1, 1f.bar, 1f.celsius, 50u, false, Location.Wheel(FRONT_LEFT)),
            Tyre.Located(now(), -30, 1, 2f.bar, 2f.celsius, 25u, false, Location.Wheel(FRONT_LEFT))
        )
        every { listenTyreUseCase.listen() } returns tyresToEmit.asFlow()
        test().listen().test {
            assertEquals(tyresToEmit[0], awaitItem())
            assertEquals(tyresToEmit[1], awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 2) { tyreDatabase.insert(any(), any()) }
        coroutineContext.cancelChildren()
    }

    @Test
    fun `No tyre emit but a cache exists`() = runTest {
        val savedTyre =
            Tyre.Located(now(), -20, 1, 1f.bar, 1f.celsius, 1u, false, Location.Wheel(FRONT_LEFT))
        every { tyreDatabase.latestByTyreLocationByVehicle(location, any()) }
            .returns(savedTyre)
        test().listen().test {
            assertEquals(savedTyre, awaitItem())
        }
        coVerify(exactly = 0) { tyreDatabase.insert(any(), any()) }
        coroutineContext.cancelChildren()
    }
}
