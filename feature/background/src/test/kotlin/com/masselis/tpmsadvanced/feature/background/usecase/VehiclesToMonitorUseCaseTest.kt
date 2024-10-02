package com.masselis.tpmsadvanced.feature.background.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


internal class VehiclesToMonitorUseCaseTest {

    private lateinit var currentVehicleUseCase: CurrentVehicleUseCase
    private lateinit var vehicleDatabase: VehicleDatabase

    @Before
    fun setup() {
        currentVehicleUseCase = mockk()
        vehicleDatabase = mockk {
            every { selectUuidIsDeleting() } returns mockk {
                every { asFlow() } returns flowOf(emptyList())
            }
            every { selectAll() } returns mockk {
                every { asFlow() } returns flowOf(emptyList())
            }
        }
    }

    private fun test() = VehiclesToMonitorUseCase(currentVehicleUseCase, vehicleDatabase)

    @Test
    fun `no background monitor`() = runTest {
        test().ignoredAndMonitored().test {
            assertEquals(emptyList<Vehicle>() to emptyList(), awaitItem())
        }
    }

    @Test
    fun `add vehicle to monitor`() = runTest {
        val vehicle = mockVehicle()
        every { vehicleDatabase.selectByUuid(vehicle.uuid) } returns mockQueryOne(vehicle)
        with(test()) {
            ignoredAndMonitored().test {
                assertEquals(emptyList<Vehicle>() to emptyList(), awaitItem())
                enableManual(vehicle.uuid)
                assertEquals(emptyList<Vehicle>() to listOf(vehicle), awaitItem())
            }
        }
    }

    @Test
    fun `add vehicle to monitor and remove it`() = runTest {
        val vehicle = mockVehicle()
        every { vehicleDatabase.selectByUuid(vehicle.uuid) } returns mockQueryOne(vehicle)
        with(test()) {
            ignoredAndMonitored().test {
                assertEquals(emptyList<Vehicle>() to emptyList(), awaitItem())
                enableManual(vehicle.uuid)
                assertEquals(emptyList<Vehicle>() to listOf(vehicle), awaitItem())
                disableManual(vehicle.uuid)
                assertEquals(emptyList<Vehicle>() to emptyList(), awaitItem())
            }
        }
    }
}
