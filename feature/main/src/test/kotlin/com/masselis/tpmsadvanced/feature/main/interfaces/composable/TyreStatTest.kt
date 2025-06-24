package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

internal class TyreStatTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material3.DayNight.NoActionBar",
    )

    @Before
    fun setup() {
        Locale.setDefault(Locale.FRANCE)
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
    }

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
