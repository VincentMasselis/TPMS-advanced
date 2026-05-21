package com.masselis.tpmsadvanced.interfaces.composable

import app.cash.paparazzi.Paparazzi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

internal class ChooseBindingMethodTest {
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
    fun normal()    {
        paparazzi.snapshot {
            ChooseBindingMethodPreview()
        }
    }
}
