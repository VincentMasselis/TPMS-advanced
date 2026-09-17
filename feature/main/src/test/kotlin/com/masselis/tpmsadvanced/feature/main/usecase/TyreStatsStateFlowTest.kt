package com.masselis.tpmsadvanced.feature.main.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class TyreStatsStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    context(scope: TestScope)
    private fun test(
        timestamp: Double,
        sensorId: Int,
    ): TyreStatsStateFlow {
        val atmosphereUseCase = mockk<TyreAtmosphereUseCase> {
            every { listen() } returns flowOf(
                TyreAtmosphere(
                    timestamp = timestamp,
                    sensorId = sensorId,
                    pressure = 2f.bar,
                    temperature = 25f.celsius,
                )
            )
        }

        val rangeUseCase = mockk<VehicleRangesUseCase> {
            every { highTemp } returns MutableStateFlow(90f.celsius)
            every { lowPressure } returns MutableStateFlow(1f.bar)
            every { highPressure } returns MutableStateFlow(3f.bar)
        }

        val unitPreferences = mockk<UnitPreferences> {
            every { pressure } returns MutableStateFlow(PressureUnit.BAR)
            every { temperature } returns MutableStateFlow(TemperatureUnit.CELSIUS)
        }

        return TyreStatsStateFlow(
            atmosphereUseCase = atmosphereUseCase,
            rangeUseCase = rangeUseCase,
            unitPreferences = unitPreferences,
            scope = scope.backgroundScope,
        )
    }

    @Test
    fun `preserves sensor id and timestamp`() = runTest {
        val timestamp = 1_726_483_200.0
        val sensorId = 0x562D00

        test(timestamp, sensorId).test {
            assertIs<State.NotDetected>(awaitItem())

            val state = awaitItem()
            assertIs<State.Normal>(state)

            assertEquals(timestamp, state.timestamp)
            assertEquals(sensorId, state.sensorId)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
