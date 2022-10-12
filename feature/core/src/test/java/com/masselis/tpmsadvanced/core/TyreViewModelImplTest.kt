package com.masselis.tpmsadvanced.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@OptIn(ExperimentalCoroutinesApi::class)
internal class TyreViewModelImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var tyreAtmosphereUseCase: TyreAtmosphereUseCase
    private lateinit var atmosphereRangePreferences: AtmosphereRangePreferences
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        tyreAtmosphereUseCase = mockk {
            every { listen() } returns emptyFlow()
        }
        atmosphereRangePreferences = mockk {
            every { lowTempFlow } returns MutableStateFlow(20f.celsius)
            every { normalTempFlow } returns MutableStateFlow(45f.celsius)
            every { highTempFlow } returns MutableStateFlow(90f.celsius)
            every { lowPressureFlow } returns MutableStateFlow(1f.bar)
            every { highPressureFlow } returns MutableStateFlow(3f.bar)
        }
        savedStateHandle = SavedStateHandle()
    }

    private fun test() = TyreViewModelImpl(
        tyreAtmosphereUseCase,
        atmosphereRangePreferences,
        500.milliseconds.toJavaDuration(),
        savedStateHandle
    )

    private fun setAtmosphere(pressure: Pressure, temperature: Temperature) =
        every { tyreAtmosphereUseCase.listen() }.returns(
            flowOf(TyreAtmosphere(now(), pressure, temperature))
        )

    @Test
    fun notDetected() = runTest {
        assertIs<State.NotDetected>(test().stateFlow.value)
    }

    @Test
    fun belowRangeTemperature() = runTest {
        setAtmosphere(2f.bar, (-10f).celsius)
        assertIs<State.Normal.BlueToGreen>(test().stateFlow.value)
    }

    @Test
    fun normalTemperature() = runTest {
        setAtmosphere(2f.bar, 25f.celsius)
        assertIs<State.Normal.BlueToGreen>(test().stateFlow.value)
    }

    @Test
    fun highTemperature() = runTest {
        setAtmosphere(2f.bar, 60f.celsius)
        assertIs<State.Normal.GreenToRed>(test().stateFlow.value)
    }

    @Test
    fun ultraHighTemperature() = runTest {
        setAtmosphere(2f.bar, 115f.celsius)
        assertIs<State.Alerting>(test().stateFlow.value)
    }

    @Test
    fun noPressure() = runTest {
        setAtmosphere(0f.bar, 45f.celsius)
        assertIs<State.Alerting>(test().stateFlow.value)
    }

    @Test
    fun lowPressure() = runTest {
        setAtmosphere(0.8f.bar, 45f.celsius)
        assertIs<State.Alerting>(test().stateFlow.value)
    }

    @Test
    fun highPressure() = runTest {
        setAtmosphere(4f.bar, 45f.celsius)
        assertIs<State.Alerting>(test().stateFlow.value)
    }

    @Test
    fun normalPressure() = runTest {
        setAtmosphere(2f.bar, 45f.celsius)
        assertIs<State.Normal>(test().stateFlow.value)
    }

    @Test
    fun obsolete() = runTest {
        setAtmosphere(2f.bar, 35f.celsius)
        val vm = test()
        assertIs<State.Normal.BlueToGreen>(vm.stateFlow.value)
        delay(1.seconds)
        assertIs<State.NotDetected>(vm.stateFlow.value)
    }
}
