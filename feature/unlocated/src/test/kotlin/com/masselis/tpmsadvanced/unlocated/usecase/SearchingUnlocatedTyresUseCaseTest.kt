package com.masselis.tpmsadvanced.unlocated.usecase

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class SearchingUnlocatedTyresUseCaseTest {
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var sensorDatabase: SensorDatabase
    private lateinit var currentVehicleUseCase: CurrentVehicleUseCase

    @Before
    fun setup() {
        scanner = mockk {
            every { highDutyScan() } returns emptyFlow()
        }
        sensorDatabase = mockk {
            every { selectListByVehicleId(any()) } returns MutableStateFlow(emptyList())
            every { selectListExcludingVehicleId(any()) } returns MutableStateFlow(emptyList())
        }
        currentVehicleUseCase = mockk()
    }

    private fun test() = SearchingUnlocatedTyresUseCase(scanner, sensorDatabase, currentVehicleUseCase)

    @Test
    fun `no sensor, no tyre`() = runTest {
        test().search()
    }
}