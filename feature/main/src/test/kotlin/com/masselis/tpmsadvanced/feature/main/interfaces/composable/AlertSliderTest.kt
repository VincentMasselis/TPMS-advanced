package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.PressureRangeSliderPreview
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.TemperatureSliderPreview
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
