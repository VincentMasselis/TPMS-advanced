package com.masselis.tpmsadvanced.feature.qrcode.usecase

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SensorIdParserTest {

    @Test
    fun `parses Bekubee sensor id`() {
        assertEquals(
            0x562D00,
            SensorIdParser.parse("002D56")
        )
    }

    @Test
    fun `parses second Bekubee sensor id`() {
        assertEquals(
            0x632E00,
            SensorIdParser.parse("002E63")
        )
    }

    @Test
    fun `accepts lowercase hexadecimal`() {
        assertEquals(
            0xEFCDAB,
            SensorIdParser.parse("abcdef")
        )
    }

    @Test
    fun `accepts uppercase hexadecimal`() {
        assertEquals(
            0xEFCDAB,
            SensorIdParser.parse("ABCDEF")
        )
    }

    @Test
    fun `trims surrounding whitespace`() {
        assertEquals(
            0x562D00,
            SensorIdParser.parse("  002D56  ")
        )
    }

    @Test
    fun `rejects too short id`() {
        assertNull(SensorIdParser.parse("12345"))
    }

    @Test
    fun `rejects too long id`() {
        assertNull(SensorIdParser.parse("1234567"))
    }

    @Test
    fun `rejects non hexadecimal id`() {
        assertNull(SensorIdParser.parse("ZZZZZZ"))
    }
}
