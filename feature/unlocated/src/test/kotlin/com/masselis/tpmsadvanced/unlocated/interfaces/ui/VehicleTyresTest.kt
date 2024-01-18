package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

internal class VehicleTyresTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig(500, 250),
        theme = "android:Theme.Material3.DayNight.NoActionBar",
    )


    @Test
    fun car() {
        paparazzi.snapshot {
            CarPreview()
        }
    }

    @Test
    fun singleAxleTrailer() {
        paparazzi.snapshot {
            SingleAxleTrailerPreview()
        }
    }

    @Test
    fun motorcycle() {
        paparazzi.snapshot {
            MotorcyclePreview()
        }
    }

    @Test
    fun tadpoleThreeWheeler() {
        paparazzi.snapshot {
            TadpoleThreeWheelerPreview()
        }
    }

    @Test
    fun deltaThreeWheeler() {
        paparazzi.snapshot {
            DeltaThreeWheelerPreview()
        }
    }

    @Test
    fun carNano() {
        paparazzi.snapshot {
            CarNanoPreview()
        }
    }

    @Test
    fun carMinus() {
        paparazzi.snapshot {
            CarMinusPreview()
        }
    }

    @Test
    fun carAverage() {
        paparazzi.snapshot {
            CarAveragePreview()
        }
    }

    @Test
    fun carBig() {
        paparazzi.snapshot {
            CarBigPreview()
        }
    }
}
