package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// 0xFF: Manufacturer Specific Data, the values below are the payload *after* the company ID 0x022B
// (i.e. what ScanRecord.getManufacturerSpecificData(0x022B) returns)
@OptIn(ExperimentalStdlibApi::class)
internal class TeslaIdOnlyTest {

    private fun decode(
        manufacturerData: String,
        manufacturerId: Int = RawTeslaIdOnly.MANUFACTURER_ID,
        address: String = "BC:6A:29:00:00:01",
    ) = RawTeslaIdOnly(
        mockScanResult(
            mockScanRecord = mockScanRecord(
                mockManufacturerData = manufacturerData.hexToByteArray(),
                mockManufacturerId = manufacturerId,
            ),
            mockAddress = address,
        )
    )

    @Test
    fun `real sleep frame is decoded`() {
        // Captured from a fitted Autel MX-Sensor BLE: 020106 06FF2B02 01FE03
        decode("01FE03")
            .let { assertNotNull(it) }
            .asTyre()
            .let { assertIs<ScannerRecord.Impl>(it) }
            .also { assertEquals(-60, it.rssi) }
    }

    @Test
    fun `full length frame with a sleep status is decoded`() {
        assertNotNull(decode("01FE03860148EA0B"))
    }

    @Test
    fun `frame with an awake status is ignored`() {
        assertNull(decode("01020A860148EA0B"))
    }

    @Test
    fun `payload too short to contain a status is ignored`() {
        assertNull(decode("01FE"))
    }

    @Test
    fun `payload longer than 8 bytes is ignored`() {
        assertNull(decode("01FE03860148EA0B00"))
    }

    @Test
    fun `payload from another company id is ignored`() {
        assertNull(decode("01FE03", manufacturerId = 0x00BF))
    }

    @Test
    fun `sensor id is derived from the mac address`() {
        assertEquals(
            decode("01FE03", address = "BC:6A:29:00:00:01")?.id(),
            decode("01FE03", address = "BC:6A:29:00:00:01")?.id(),
        )
        assertNotEquals(
            decode("01FE03", address = "BC:6A:29:00:00:01")?.id(),
            decode("01FE03", address = "BC:6A:29:00:00:02")?.id(),
        )
    }
}
