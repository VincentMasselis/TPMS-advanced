package com.masselis.tpmsadvanced.interfaces.composable

import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

internal class ChooseBindingMethodTest {
    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material3.DayNight.NoActionBar",
    )

    @Test
    fun normal()    {
        paparazzi.snapshot {
            ChooseBindingMethodPreview()
        }
    }
}
