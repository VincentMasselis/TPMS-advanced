package com.masselis.tpmsadvanced.core

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.feature.usecase.TyreUseCaseImpl
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class TyreUseCaseImplTest {

    private lateinit var vehicle: Vehicle
    private lateinit var locations: Set<SensorLocation>
    private lateinit var tyreDatabase: TyreDatabase
    private lateinit var scanner: BluetoothLeScanner
    private var tyresToEmit: List<Tyre> = emptyList()

    private fun CoroutineScope.test() = TyreUseCaseImpl(
        vehicle,
        locations,
        tyreDatabase,
        scanner,
        this
    )

    @Before
    fun setup() {
        vehicle = mockk {
            val uuid = UUID.randomUUID()
            every { this@mockk.uuid } returns uuid
        }
        locations = SensorLocation.entries.toSet()
        tyreDatabase = mockk {
            coEvery { insert(any(), any()) } returns Unit
            every { latestByTyreLocationByVehicle(any(), any()) } returns null
        }
        scanner = mockk {
            fun createFlow() = channelFlow {
                tyresToEmit.forEach { tyre ->
                    // Like a real Sysgration sensor, the value is emit several times
                    repeat(10) { count ->
                        send(tyre.copy(timestamp = tyre.timestamp + count.div(1000.0)))
                    }
                }
                awaitClose()
            }
            every { normalScan() } returns createFlow()
            every { highDutyScan() } returns createFlow()
        }
    }

    @Test
    fun normal() = runTest {
        tyresToEmit = listOf(
            Tyre(now(), FRONT_LEFT, 1, 1f.bar, 1f.celsius, 1u, false),
            Tyre(now(), FRONT_RIGHT, 2, 2f.bar, 2f.celsius, 2u, false)
        )
        test().listen().test {
            assertEquals(awaitItem(), tyresToEmit[0])
            assertEquals(awaitItem(), tyresToEmit[1])
        }
        coVerify(exactly = 2) { tyreDatabase.insert(any(), any()) }
        coroutineContext.cancelChildren()
    }

    @Test
    fun filtered() = runTest {
        tyresToEmit = listOf(
            Tyre(now(), FRONT_LEFT, 1, 1f.bar, 1f.celsius, 1u, false),
            Tyre(now(), FRONT_RIGHT, 2, 2f.bar, 2f.celsius, 2u, false)
        )
        locations = setOf(REAR_LEFT, REAR_RIGHT)
        test().listen().test {
            advanceUntilIdle()
            expectNoEvents()
        }
        coVerify(exactly = 0) { tyreDatabase.insert(any(), any()) }
        coroutineContext.cancelChildren()
    }

    @Test
    fun startWithCache() = runTest {
        locations = setOf(FRONT_LEFT)
        val tyre = Tyre(now(), FRONT_LEFT, 1, 1f.bar, 1f.celsius, 1u, false)
        every { tyreDatabase.latestByTyreLocationByVehicle(locations, any()) } returns tyre
        test().listen().test {
            assertEquals(tyre, awaitItem())
        }
        coVerify(exactly = 0) { tyreDatabase.insert(any(), any()) }
        coroutineContext.cancelChildren()
    }

    @Test
    fun singleBluetoothScan() = runTest {
        turbineScope {
            val uc = test()
            val first = uc.listen().testIn(backgroundScope)
            val second = uc.listen().testIn(backgroundScope)
            val third = uc.listen().testIn(backgroundScope)
            advanceUntilIdle()
            @Suppress("IgnoredReturnValue")
            coVerify(exactly = 1) { scanner.highDutyScan() }
            first.cancel()
            second.cancel()
            third.cancel()
            coroutineContext.cancelChildren()
        }
    }
}
