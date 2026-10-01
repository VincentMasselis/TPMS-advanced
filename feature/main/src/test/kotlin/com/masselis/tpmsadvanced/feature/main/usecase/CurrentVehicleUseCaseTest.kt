package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

internal class CurrentVehicleUseCaseTest {

    private lateinit var database: VehicleDatabase

    // insertAsCurrent() never reads stateFlow; passing a stub bypasses the default value's
    // eager VehicleComponent construction, which needs the DI graph and isn't available here.
    private fun test() = CurrentVehicleUseCase(database, mockk<StateFlow<VehicleComponent>>())

    @Before
    fun setup() {
        database = mockk {
            coEvery { insert(any(), any(), any(), any(), any()) } returns Unit
        }
    }

    @Test
    fun `inserting a car does not request the rear pressure override`() = runTest {
        test().insertAsCurrent("My car", Vehicle.Kind.CAR)
        coVerify {
            database.insert(any<UUID>(), Vehicle.Kind.CAR, "My car", true, false)
        }
    }

    @Test
    fun `inserting a single axle trailer does not request the rear pressure override`() = runTest {
        test().insertAsCurrent("My trailer", Vehicle.Kind.SINGLE_AXLE_TRAILER)
        coVerify {
            database.insert(any<UUID>(), Vehicle.Kind.SINGLE_AXLE_TRAILER, "My trailer", true, false)
        }
    }

    @Test
    fun `inserting a motorcycle requests the rear pressure override by default`() = runTest {
        test().insertAsCurrent("My motorcycle", Vehicle.Kind.MOTORCYCLE)
        coVerify {
            database.insert(any<UUID>(), Vehicle.Kind.MOTORCYCLE, "My motorcycle", true, true)
        }
    }

    @Test
    fun `inserting a three wheeler requests the rear pressure override by default`() = runTest {
        test().insertAsCurrent("My trike", Vehicle.Kind.TADPOLE_THREE_WHEELER)
        coVerify {
            database.insert(any<UUID>(), Vehicle.Kind.TADPOLE_THREE_WHEELER, "My trike", true, true)
        }
    }
}
