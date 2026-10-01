package com.masselis.tpmsadvanced.data.vehicle.model

import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PressureCalibrationTest {

    @Test
    fun `multiplies then adds the offset`() {
        assertEquals(
            230f,
            PressureCalibration(offset = 10f.kpa, multiplier = 1.1f).applyTo(200f.kpa).kpa,
            0.001f,
        )
    }

    @Test
    fun `negative offset lowers the pressure`() {
        assertEquals(190f, PressureCalibration((-10f).kpa, 1f).applyTo(200f.kpa).kpa, 0.001f)
    }

    @Test
    fun `leaves a missing pressure at zero`() {
        assertEquals(0f.kpa, PressureCalibration(10f.kpa, 1.1f).applyTo(0f.kpa))
    }

    @Test
    fun `never goes below zero`() {
        assertEquals(0f.kpa, PressureCalibration((-50f).kpa, 1f).applyTo(20f.kpa))
    }

    @Test
    fun `adjusts nothing with the default values`() {
        assertFalse(PressureCalibration(0f.kpa, 1f).adjusts)
    }

    @Test
    fun `adjusts nothing despite float errors`() {
        assertFalse(PressureCalibration(0.00001f.kpa, 100 * 0.01f).adjusts)
    }

    @Test
    fun `adjusts with an offset or a multiplier`() {
        assertTrue(PressureCalibration(1f.kpa, 1f).adjusts)
        assertTrue(PressureCalibration(0f.kpa, 1.01f).adjusts)
    }
}
