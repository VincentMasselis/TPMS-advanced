package com.masselis.tpmsadvanced.feature.main.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.PressureCalibration
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class TyreAtmosphereUseCaseTest {

    private lateinit var listenTyreUseCase: ListenTyreUseCase
    private lateinit var calibration: MutableStateFlow<PressureCalibration?>
    private lateinit var calibrationUseCase: VehicleCalibrationUseCase

    @Before
    fun setup() {
        listenTyreUseCase = mockk()
        calibration = MutableStateFlow(null)
        calibrationUseCase = mockk { every { calibration } returns this@TyreAtmosphereUseCaseTest.calibration }
    }

    private fun test() = TyreAtmosphereUseCase(listenTyreUseCase, calibrationUseCase)

    private fun setTyre(pressure: Pressure, isAlarm: Boolean = false) =
        every { listenTyreUseCase.listen() } returns flowOf(
            Tyre.Located(0.0, 0, 0, pressure, 20f.celsius, 100u, isAlarm, Wheel(FRONT_LEFT))
        )

    @Test
    fun `sends the read pressure while the calibration is off`() = runTest {
        setTyre(200f.kpa)
        test().listen().test {
            assertEquals(200f.kpa, awaitItem().pressure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `corrects the pressure while the calibration is on`() = runTest {
        setTyre(200f.kpa)
        calibration.value = PressureCalibration(10f.kpa, 1f)
        test().listen().test {
            assertEquals(210f.kpa, awaitItem().pressure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `follows a calibration change`() = runTest {
        setTyre(200f.kpa)
        test().listen().test {
            assertEquals(200f.kpa, awaitItem().pressure)
            calibration.value = PressureCalibration((-10f).kpa, 1f)
            assertEquals(190f.kpa, awaitItem().pressure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `keeps an alarm at zero whatever the offset`() = runTest {
        setTyre(200f.kpa, isAlarm = true)
        calibration.value = PressureCalibration(10f.kpa, 1f)
        test().listen().test {
            assertEquals(0f.kpa, awaitItem().pressure)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
