package com.masselis.tpmsadvanced.feature.main.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Side
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class VehicleRangesUseCaseTest {

    private lateinit var vehicle: Vehicle
    private lateinit var database: VehicleDatabase
    private lateinit var uuid: UUID

    context(scope: TestScope)
    private fun test() = VehicleRangesUseCase(vehicle, scope.backgroundScope, database)

    @Before
    fun setup() {
        val generatedUuid = UUID.randomUUID()
        uuid = generatedUuid
        vehicle = mockk { every { this@mockk.uuid } returns generatedUuid }
        database = mockk {
            every { selectLowPressure(uuid) } returns 1f.bar
            every { selectHighPressure(uuid) } returns 3f.bar
            every { selectLowTemp(uuid) } returns 20f.celsius
            every { selectNormalTemp(uuid) } returns 45f.celsius
            every { selectHighTemp(uuid) } returns 90f.celsius
            every { selectRearLowPressure(uuid) } returns null
            every { selectRearHighPressure(uuid) } returns null
            every { selectSeparateRearPressure(uuid) } returns false
            coEvery { updateLowPressure(any(), any()) } returns Unit
            coEvery { updateHighPressure(any(), any()) } returns Unit
            coEvery { updateLowTemp(any(), any()) } returns Unit
            coEvery { updateNormalTemp(any(), any()) } returns Unit
            coEvery { updateHighTemp(any(), any()) } returns Unit
            coEvery { updateRearLowPressure(any(), any()) } returns Unit
            coEvery { updateRearHighPressure(any(), any()) } returns Unit
            coEvery { updateSeparateRearPressure(any(), any()) } returns Unit
        }
    }

    @Test
    fun `front location always resolves to the shared range`() = runTest {
        val useCase = test()
        useCase.resolvedLowPressure(Wheel(FRONT_LEFT)).test {
            assertEquals(1f.bar, awaitItem())
        }
        useCase.resolvedHighPressure(Wheel(FRONT_LEFT)).test {
            assertEquals(3f.bar, awaitItem())
        }
    }

    @Test
    fun `a location without an axle always resolves to the shared range`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        useCase.resolvedLowPressure(Side(LEFT)).test {
            assertEquals(1f.bar, awaitItem())
        }
    }

    @Test
    fun `rear location resolves to the shared range when no override is set`() = runTest {
        val useCase = test()
        useCase.resolvedLowPressure(Wheel(REAR_LEFT)).test {
            assertEquals(1f.bar, awaitItem())
        }
        useCase.resolvedHighPressure(Wheel(REAR_LEFT)).test {
            assertEquals(3f.bar, awaitItem())
        }
    }

    @Test
    fun `rear location resolves to the override once set`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        useCase.rearLowPressure.value = 1.5f.bar
        useCase.rearHighPressure.value = 3.5f.bar
        useCase.resolvedLowPressure(Wheel(REAR_LEFT)).test {
            assertEquals(1.5f.bar, awaitItem())
        }
        useCase.resolvedHighPressure(Wheel(REAR_LEFT)).test {
            assertEquals(3.5f.bar, awaitItem())
        }
    }

    @Test
    fun `enabling the rear override copies the current front values`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        assertEquals(1f.bar, useCase.rearLowPressure.value)
        assertEquals(3f.bar, useCase.rearHighPressure.value)
    }

    @Test
    fun `disabling the rear override keeps both values`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        useCase.rearLowPressure.value = 1.5f.bar
        useCase.rearHighPressure.value = 3.5f.bar
        useCase.setRearOverrideEnabled(false)
        assertEquals(false, useCase.separateRearPressure.value)
        assertEquals(1.5f.bar, useCase.rearLowPressure.value)
        assertEquals(3.5f.bar, useCase.rearHighPressure.value)
    }

    @Test
    fun `rear location ignores the kept values while the override is disabled`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        useCase.rearLowPressure.value = 1.5f.bar
        useCase.rearHighPressure.value = 3.5f.bar
        useCase.setRearOverrideEnabled(false)
        useCase.resolvedLowPressure(Wheel(REAR_LEFT)).test {
            assertEquals(1f.bar, awaitItem())
        }
        useCase.resolvedHighPressure(Wheel(REAR_LEFT)).test {
            assertEquals(3f.bar, awaitItem())
        }
    }

    @Test
    fun `re-enabling the rear override restores the kept values`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        useCase.rearLowPressure.value = 1.5f.bar
        useCase.rearHighPressure.value = 3.5f.bar
        useCase.setRearOverrideEnabled(false)
        useCase.lowPressure.value = 2f.bar
        useCase.setRearOverrideEnabled(true)
        assertEquals(1.5f.bar, useCase.rearLowPressure.value)
        assertEquals(3.5f.bar, useCase.rearHighPressure.value)
    }

    @Test
    fun `rear override changes are persisted after the debounce`() = runTest {
        val useCase = test()
        useCase.rearLowPressure.value = 1.5f.bar
        useCase.rearHighPressure.value = 3.5f.bar
        advanceTimeBy(100.milliseconds)
        runCurrent()
        coVerify { database.updateRearLowPressure(1.5f.bar, uuid) }
        coVerify { database.updateRearHighPressure(3.5f.bar, uuid) }
    }

    @Test
    fun `rear override switch is persisted after the debounce`() = runTest {
        val useCase = test()
        useCase.setRearOverrideEnabled(true)
        advanceTimeBy(100.milliseconds)
        runCurrent()
        coVerify { database.updateSeparateRearPressure(true, uuid) }
    }
}
