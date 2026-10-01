package com.masselis.tpmsadvanced.feature.main.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
internal class TyreStatsStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var tyreAtmosphereUseCase: TyreAtmosphereUseCase
    private lateinit var vehicleRangesUseCase: VehicleRangesUseCase
    private lateinit var unitPreferences: UnitPreferences

    @Before
    fun setup() {
        tyreAtmosphereUseCase = mockk {
            every { listen() } returns emptyFlow()
        }
        vehicleRangesUseCase = mockk {
            every { highTemp } returns MutableStateFlow(90f.celsius)
            every { resolvedLowPressure(Wheel(FRONT_LEFT)) } returns MutableStateFlow(1f.bar)
            every { resolvedHighPressure(Wheel(FRONT_LEFT)) } returns MutableStateFlow(3f.bar)
        }
        unitPreferences = mockk {
            every { pressure } returns MutableStateFlow(BAR)
            every { temperature } returns MutableStateFlow(CELSIUS)
        }
    }

    context(scope: TestScope)
    private fun test() = TyreStatsStateFlow(
        tyreAtmosphereUseCase,
        vehicleRangesUseCase,
        Wheel(FRONT_LEFT),
        unitPreferences,
        scope.backgroundScope,
    )

    private fun setAtmosphere(pressure: Pressure, temperature: Temperature) =
        every { tyreAtmosphereUseCase.listen() }.returns(
            flowOf(TyreAtmosphere(now(), pressure, temperature))
        )

    @Test
    fun notDetected(): Unit = runTest {
        assertIs<State.NotDetected>(test().value)
    }

    @Test
    fun normalPressure(): Unit = runTest {
        setAtmosphere(2f.bar, 45f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal>(awaitItem())
        }
    }

    @Test
    fun lowPressure(): Unit = runTest {
        setAtmosphere(0.8f.bar, 45f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
        }
    }

    @Test
    fun highTemperature(): Unit = runTest {
        setAtmosphere(2f.bar, 115f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
        }
    }
}
