package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Axle
import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Located
import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Side
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import org.junit.Test
import kotlin.test.assertEquals

internal class ManySensorTest {

    /**
     * I need `ManySensor` to be explicit when `toString()` is called to ensure that the keys used
     * by the method `viewModel(key="")` are valid.
     */
    @Test
    fun toStringTest() {
        assertEquals("Axle(axle=FRONT)", "${Axle(FRONT)}")
        assertEquals("Side(side=LEFT)", "${Side(LEFT)}")
        assertEquals("Located(location=FRONT_LEFT)", "${Located(FRONT_LEFT)}")
    }
}