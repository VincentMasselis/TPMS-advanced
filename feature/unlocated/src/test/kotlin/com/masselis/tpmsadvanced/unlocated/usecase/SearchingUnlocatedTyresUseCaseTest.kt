package com.masselis.tpmsadvanced.unlocated.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.mockSensor
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.mockTyre
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.mockVehicle
import com.masselis.tpmsadvanced.unlocated.usecase.SearchingUnlocatedTyresUseCase.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SearchingUnlocatedTyresUseCaseTest {
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var sensorDatabase: SensorDatabase
    private lateinit var vehicleDatabase: VehicleDatabase
    private lateinit var vehicleUuid: UUID

    @Before
    fun setup() {
        vehicleUuid = UUID.randomUUID()
        scanner = mockk {
            every { highDutyScan() } returns MutableSharedFlow()
        }
        sensorDatabase = mockk {
            every { selectListByVehicleId(vehicleUuid) } returns MutableStateFlow(emptyList())
            every { selectListExcludingVehicleId(vehicleUuid) } returns MutableStateFlow(emptyList())
        }
        vehicleDatabase = mockk {
            every { selectBySensorId(any()) } returns MutableStateFlow(null)
        }
    }

    private fun test() = SearchingUnlocatedTyresUseCase(
        scanner,
        sensorDatabase,
        vehicleDatabase,
        vehicleUuid
    )

    @Test
    fun `nothing found`() = runTest {
        test().search().test {
            assertEquals(
                Result(emptyList(), emptyList(), emptyList()),
                awaitItem()
            )
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no tyre found but bound sensor exists`() = runTest {
        val boundSensor = mockSensor(1)
        every { sensorDatabase.selectListByVehicleId(vehicleUuid) }
            .returns(MutableStateFlow(listOf(boundSensor)))

        test().search().test {
            assertEquals(
                Result(listOf(boundSensor to null), emptyList(), emptyList()),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `only tyre found was bound to a sensor`() = runTest {
        val boundSensor = mockSensor(1)
        every { sensorDatabase.selectListByVehicleId(vehicleUuid) }
            .returns(MutableStateFlow(listOf(boundSensor)))

        val foundTyre = mockTyre(1)
        every { scanner.highDutyScan() } returns MutableStateFlow(foundTyre)

        test().search().test {
            awaitItem()
            assertEquals(
                Result(listOf(boundSensor to foundTyre), emptyList(), emptyList()),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a tyre was found and a different bound sensor exists`() = runTest {
        val boundSensor = mockSensor(1)
        every { sensorDatabase.selectListByVehicleId(vehicleUuid) }
            .returns(MutableStateFlow(listOf(boundSensor)))

        val foundTyre = mockTyre(2)
        every { scanner.highDutyScan() } returns MutableStateFlow(foundTyre)

        test().search().test {
            awaitItem()
            assertEquals(
                Result(listOf(boundSensor to null), listOf(foundTyre), emptyList()),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no bound sensor but tyre found`() = runTest {
        val foundTyre = mockTyre(1)
        every { scanner.highDutyScan() } returns MutableStateFlow(foundTyre)

        test().search().test {
            awaitItem()
            assertEquals(
                Result(emptyList(), listOf(foundTyre), emptyList()),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `tyre found was bound to an other vehicle`() = runTest {
        val foundTyre = mockTyre(1)
        every { scanner.highDutyScan() } returns MutableStateFlow(foundTyre)

        val otherVehicleSensor = mockSensor(1)
        every { sensorDatabase.selectListExcludingVehicleId(vehicleUuid) }
            .returns(MutableStateFlow(listOf(otherVehicleSensor)))

        val otherVehicle = mockVehicle()
        every { vehicleDatabase.selectBySensorId(otherVehicleSensor.id) }
            .returns(MutableStateFlow(otherVehicle))

        test().search().test {
            awaitItem()
            assertEquals(
                Result(
                    emptyList(),
                    emptyList(),
                    listOf(Triple(otherVehicle, otherVehicleSensor, foundTyre))
                ),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
