package com.masselis.tpmsadvanced.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.model.TyreAtmosphere
import com.masselis.tpmsadvanced.usecase.AtmosphereRangeUseCase
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@OptIn(ExperimentalCoroutinesApi::class)
class TyreViewModelImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var tyreAtmosphereUseCase: TyreAtmosphereUseCase
    private lateinit var atmosphereRangeUseCase: AtmosphereRangeUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        tyreAtmosphereUseCase = mock {
            on { mock.listen() } doReturn emptyFlow()
        }
        atmosphereRangeUseCase = mock {
            on { mock.lowTempFlow } doReturn MutableStateFlow(20f.celsius)
            on { mock.normalTempFlow } doReturn MutableStateFlow(45f.celsius)
            on { mock.highTempFlow } doReturn MutableStateFlow(90f.celsius)
            on { mock.lowPressureFlow } doReturn MutableStateFlow(1f.bar)
        }
        savedStateHandle = SavedStateHandle()
    }

    private fun test() = TyreViewModelImpl(
        tyreAtmosphereUseCase,
        atmosphereRangeUseCase,
        200.milliseconds.toJavaDuration(),
        savedStateHandle
    )

    private fun setAtmosphere(pressure: Float, temperature: Float) =
        whenever(tyreAtmosphereUseCase.listen()).doReturn(
            flowOf(
                TyreAtmosphere(
                    System.currentTimeMillis().div(1000.0),
                    Pressure(pressure),
                    Temperature(temperature)
                )
            )
        )

    @Test
    fun notDetected() = runTest {
        Assert.assertEquals(State.NotDetected, test().stateFlow.value)
    }

    @Test
    fun belowRangeTemperature() = runTest {
        setAtmosphere(200f, 15f)
        assert(test().stateFlow.value is State.Normal.BlueToGreen)
    }

    @Test
    fun normalTemperature() = runTest {
        setAtmosphere(200f, 25f)
        assert(test().stateFlow.value is State.Normal.BlueToGreen)
    }

    @Test
    fun highTemperature() = runTest {
        setAtmosphere(200f, 60f)
        assert(test().stateFlow.value is State.Normal.GreenToRed)
    }

    @Test
    fun ultraHighTemperature() = runTest {
        setAtmosphere(200f, 115f)
        assert(test().stateFlow.value is State.Alerting)
    }

    @Test
    fun noPressure() = runTest {
        setAtmosphere(0f, 45f)
        assert(test().stateFlow.value is State.Alerting)
    }

    @Test
    fun obsolete() = runTest {
        setAtmosphere(200f, 35f)
        val vm = test()
        assert(vm.stateFlow.value is State.Normal.BlueToGreen)
        delay(400.milliseconds)
        assert(vm.stateFlow.value is State.NotDetected)
    }
}