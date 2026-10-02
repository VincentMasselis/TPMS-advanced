package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.Location
import org.junit.Test
import kotlin.test.assertEquals

internal class VehicleKindLocationTest {

    /**
     * I need `Location` to be unique when `toString()` is called to ensure that the keys used
     * by the method `viewModel(key="")` are valid.
     */
    @Test
    fun toStringTest() {
        val locations = Location.Wheel.entries + Location.Axle.entries + Location.Side.entries
        assertEquals(locations.size, locations.map { "$it" }.toSet().size)
    }
}
