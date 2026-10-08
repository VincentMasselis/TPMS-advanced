package com.masselis.tpmsadvanced.feature.main.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
internal class TyreIconStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var location: Location
    private lateinit var tyreAtmosphereUseCase: TyreAtmosphereUseCase
    private lateinit var vehicleRangesUseCase: VehicleRangesUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        location = Location.Wheel.FRONT_LEFT
        tyreAtmosphereUseCase = mockk {
            every { listen() } returns emptyFlow()
        }
        vehicleRangesUseCase = mockk {
            every { lowTemp } returns MutableStateFlow(20f.celsius)
            every { normalTemp } returns MutableStateFlow(45f.celsius)
            every { highTemp } returns MutableStateFlow(90f.celsius)
            every { resolvePressures(location) } returns flowOf(1f.bar to 3f.bar)
        }
        savedStateHandle = SavedStateHandle()
    }

    context(scope: TestScope)
    private fun test() = TyreIconStateFlow(
        location,
        tyreAtmosphereUseCase,
        vehicleRangesUseCase,
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
    fun belowRangeTemperature(): Unit = runTest {
        setAtmosphere(2f.bar, (-10f).celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal.BlueToGreen>(awaitItem())
        }
    }

    @Test
    fun normalTemperature(): Unit = runTest {
        setAtmosphere(2f.bar, 25f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal.BlueToGreen>(awaitItem())
        }
    }

    @Test
    fun highTemperature(): Unit = runTest {
        setAtmosphere(2f.bar, 60f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal.GreenToRed>(awaitItem())
        }
    }

    @Test
    fun ultraHighTemperature(): Unit = runTest {
        setAtmosphere(2f.bar, 115f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
        }
    }

    @Test
    fun noPressure(): Unit = runTest {
        setAtmosphere(0f.bar, 45f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
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
    fun highPressure(): Unit = runTest {
        setAtmosphere(4f.bar, 45f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
        }
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
    fun rearNormalPressure(): Unit = runTest {
        location = Location.Wheel.REAR_LEFT
        setAtmosphere(2f.bar, 45f.celsius)
        every { vehicleRangesUseCase.resolvePressures(Location.Wheel.REAR_LEFT) } returns flowOf(0.5f.bar to 3f.bar)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal>(awaitItem())
        }
    }

    @Test
    fun rearHighPressure(): Unit = runTest {
        location = Location.Wheel.REAR_LEFT
        setAtmosphere(4f.bar, 45f.celsius)
        every { vehicleRangesUseCase.resolvePressures(Location.Wheel.REAR_LEFT) } returns flowOf(0.5f.bar to 2f.bar)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Alerting>(awaitItem())
        }
    }

    @Test
    fun obsolete(): Unit = runTest {
        setAtmosphere(2f.bar, 35f.celsius)
        test().test {
            assertIs<State.NotDetected>(awaitItem())
            assertIs<State.Normal.BlueToGreen>(awaitItem())
            advanceTimeBy(1.hours)
            assertIs<State.NotDetected>(awaitItem())
        }
    }
}
