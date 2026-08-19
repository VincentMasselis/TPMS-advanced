package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class A827Test {

    private fun decode(packet: String) = RawA827(
        mockk {
            every { rssi } returns -60
            every { scanRecord } returns mockk {
                every { serviceUuids } returns mockk {
                    every { contains(any()) } returns true
                }
                every { bytes } returns packet.hexToByteArray()
            }
        }
    )

    @Test
    fun `decodes real A827 sensor packet`() {
        val raw = decode(
            "050854504D530CFF0200AF440089002D56087B030327A8"
        )

        assertNotNull(raw)

        val tyre = raw!!.asTyre()

        // QR / printed sensor ID: 002D56
        assertEquals(0x562D00, tyre.sensorId)

        // Raw pressure 0x0089 = 137; 137 - 100 = 37 kPa
        assertEquals(37f, tyre.pressure.kpa, 0.01f)

        // Raw temperature 0x44 = 68; 68 - 50 = 18°C
        assertEquals(18f, tyre.temperature.celsius, 0.01f)
		// Raw battery value 0xAF = 175; 175 * 0.01 + 1.22 = 2.97 V
		// Battery values are stored in tenths of a volt.
		assertEquals(30u.toUShort(), tyre.battery)
    }

    @Test
    fun `decodes temperature change without pressure change`() {
        val raw = decode(
            "050854504D530CFF0600B14D0089002D563A76030327A8"
        )

        assertNotNull(raw)

        val tyre = raw!!.asTyre()

        assertEquals(0x562D00, tyre.sensorId)
        assertEquals(37f, tyre.pressure.kpa, 0.01f)

        // Raw temperature 0x4D = 77; 77 - 50 = 27°C
        assertEquals(27f, tyre.temperature.celsius, 0.01f)
    }
}
