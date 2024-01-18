package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

internal class AlertSliderTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material3.DayNight.NoActionBar"
    )

    @Test
    fun pressureRangeSlider() {
        paparazzi.snapshot {
            PressureRangeSliderPreview()
        }
    }

    @Test
    fun temperatureSlider() {
        paparazzi.snapshot {
            TemperatureSliderPreview()
        }
    }
}
