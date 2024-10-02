package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Axle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Side
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import org.junit.Test
import kotlin.test.assertEquals

internal class VehicleKindLocationTest {

    /**
     * I need `Location` to be explicit when `toString()` is called to ensure that the keys used
     * by the method `viewModel(key="")` are valid.
     */
    @Test
    fun toStringTest() {
        assertEquals("Axle(axle=FRONT)", "${Axle(FRONT)}")
        assertEquals("Side(side=LEFT)", "${Side(LEFT)}")
        assertEquals("Wheel(location=FRONT_LEFT)", "${Wheel(FRONT_LEFT)}")
    }
}
