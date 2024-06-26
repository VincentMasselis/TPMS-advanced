package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.TyreStatAlertingPreview
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.TyreStatNormalPreview
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.TyreStatNotDetectedPreview
import org.junit.Rule
import org.junit.Test

internal class TyreStatTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material3.DayNight.NoActionBar",
    )

    @Test
    fun notDetected() {
        paparazzi.snapshot {
            TyreStatNotDetectedPreview()
        }
    }

    @Test
    fun normal() {
        paparazzi.snapshot {
            TyreStatNormalPreview()
        }
    }

    @Test
    fun alerting() {
        paparazzi.snapshot {
            TyreStatAlertingPreview()
        }
    }
}
