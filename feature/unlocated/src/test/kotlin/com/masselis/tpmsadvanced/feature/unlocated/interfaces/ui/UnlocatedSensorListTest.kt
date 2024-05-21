package com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui

import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.AllWheelsAreAlreadyBoundPreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.CompletedPreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.IssuePreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.SearchingFoundMultipleTyrePreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.SearchingFoundOnlyBoundTyrePreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.SearchingFoundSingleTyrePreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.SearchingNoResultPreview
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.UnplugEverySensorPreview
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

internal class UnlocatedSensorListTest {
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
    fun allWheelsAreAlreadyBound() {
        paparazzi.snapshot {
            AllWheelsAreAlreadyBoundPreview()
        }
    }

    @Test
    fun unplugEverySensor() {
        paparazzi.snapshot {
            UnplugEverySensorPreview()
        }
    }

    @Test
    fun searchingNoResult() {
        paparazzi.snapshot {
            SearchingNoResultPreview()
        }
    }

    @Test
    fun searchingFoundSingleTyre() {
        paparazzi.snapshot {
            SearchingFoundSingleTyrePreview()
        }
    }

    @Test
    fun searchingFoundMultipleTyre() {
        paparazzi.snapshot {
            SearchingFoundMultipleTyrePreview()
        }
    }

    @Test
    fun searchingFoundOnlyBoundTyre() {
        paparazzi.snapshot {
            SearchingFoundOnlyBoundTyrePreview()
        }
    }

    @Test
    fun completed() {
        paparazzi.snapshot {
            CompletedPreview()
        }
    }

    @Test
    fun issue() {
        paparazzi.snapshot {
            IssuePreview()
        }
    }
}
