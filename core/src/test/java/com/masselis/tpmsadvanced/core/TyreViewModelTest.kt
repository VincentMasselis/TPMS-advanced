package com.masselis.tpmsadvanced.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreAtmosphere
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import kotlin.time.toJavaDuration

@OptIn(ExperimentalCoroutinesApi::class)
class TyreViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var atmosphereUseCase: TyreAtmosphereUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        atmosphereUseCase = mock {
            on { mock.listen() } doReturn emptyFlow()
        }
        savedStateHandle = SavedStateHandle()
    }

    private fun test() = TyreViewModel(
        atmosphereUseCase,
        100.milliseconds.toJavaDuration(),
        savedStateHandle
    )

    private fun setAtmosphere(pressure: Float, temperature: Float) =
        whenever(atmosphereUseCase.listen())
            .doReturn(flowOf(TyreAtmosphere(Pressure(pressure), Temperature(temperature))))

    @Test
    fun notDetected() = runTest {
        Assert.assertEquals(TyreViewModel.State.NotDetected, test().stateFlow.value)
    }

    @Test
    fun belowRangeTemperature() = runTest {
        setAtmosphere(200f, 15f)
        assert(test().stateFlow.value is TyreViewModel.State.Normal.BlueToGreen)
    }

    @Test
    fun normalTemperature() = runTest {
        setAtmosphere(200f, 25f)
        assert(test().stateFlow.value is TyreViewModel.State.Normal.BlueToGreen)
    }

    @Test
    fun highTemperature() = runTest {
        setAtmosphere(200f, 60f)
        assert(test().stateFlow.value is TyreViewModel.State.Normal.GreenToRed)
    }

    @Test
    fun ultraHighTemperature() = runTest {
        setAtmosphere(200f, 115f)
        assert(test().stateFlow.value is TyreViewModel.State.Alerting)
    }

    @Test
    fun noPressure() = runTest {
        setAtmosphere(0f, 45f)
        assert(test().stateFlow.value is TyreViewModel.State.Alerting)
    }

    @Test
    fun obsolete() = runTest {
        setAtmosphere(200f, 35f)
        val vm = test()
        assert(vm.stateFlow.value is TyreViewModel.State.Normal.BlueToGreen)
        delay(200.milliseconds)
        assert(vm.stateFlow.value is TyreViewModel.State.Obsolete)
    }
}