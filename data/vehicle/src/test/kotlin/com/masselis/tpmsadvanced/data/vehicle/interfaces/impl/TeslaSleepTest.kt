package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// 0xFF: Manufacturer Specific Data, the values below are the payload *after* the company ID 0x022B
// (i.e. what ScanRecord.getManufacturerSpecificData(0x022B) returns)
@OptIn(ExperimentalStdlibApi::class)
internal class TeslaSleepTest {

    private fun scanResult(
        manufacturerData: String,
        manufacturerId: Int = RawTesla.MANUFACTURER_ID,
        address: String = "BC:6A:29:00:00:01",
    ) = mockScanResult(
        mockScanRecord = mockScanRecord(
            mockManufacturerData = manufacturerData.hexToByteArray(),
            mockManufacturerId = manufacturerId,
        ),
        mockAddress = address,
    )

    @Test
    fun `real sleep frame is decoded`() {
        // Captured from a fitted Autel MX-Sensor BLE: 020106 06FF2B02 01FE03
        RawTeslaSleep(scanResult("01FE03"))
            .let { assertNotNull(it) }
            .asTyre()
            .let { assertIs<ScannerRecord.Impl>(it) }
            .also { assertEquals(-60, it.rssi) }
    }

    @Test
    fun `full length frame with a sleep status is decoded`() {
        assertNotNull(RawTeslaSleep(scanResult("01FE03860148EA0B")))
    }

    @Test
    fun `awake frame is ignored`() {
        assertNull(RawTeslaSleep(scanResult("01020A860148EA0B")))
    }

    @Test
    fun `payload too short to contain a status is ignored`() {
        assertNull(RawTeslaSleep(scanResult("01FE")))
    }

    @Test
    fun `payload longer than an awake frame is ignored`() {
        assertNull(RawTeslaSleep(scanResult("01FE03860148EA0B00")))
    }

    @Test
    fun `payload from another company id is ignored`() {
        assertNull(RawTeslaSleep(scanResult("01FE03", manufacturerId = 0x00BF)))
    }

    @Test
    fun `sleep and awake frames from the same sensor share the sensor id`() {
        assertEquals(
            RawTeslaAwake(scanResult("01020A860148EA0B"))?.asTyre()?.sensorId,
            RawTeslaSleep(scanResult("01FE03"))?.asTyre()?.sensorId,
        )
    }
}
