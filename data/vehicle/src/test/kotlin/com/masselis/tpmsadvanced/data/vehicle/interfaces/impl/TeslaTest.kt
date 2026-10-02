package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// 0xFF: Manufacturer Specific Data, the values below are the payload *after* the company ID 0x022B
// (i.e. what ScanRecord.getManufacturerSpecificData(0x022B) returns)
@OptIn(ExperimentalStdlibApi::class)
internal class TeslaTest {

    private fun decode(
        manufacturerData: String,
        manufacturerId: Int = RawTesla.MANUFACTURER_ID,
        address: String = "BC:6A:29:00:00:01",
    ) = RawTesla(
        mockScanResult(
            mockScanRecord = mockScanRecord(
                mockManufacturerData = manufacturerData.hexToByteArray(),
                mockManufacturerId = manufacturerId,
            ),
            mockAddress = address,
        )
    )

    @Test
    fun `awake frame is decoded`() {
        // Synthetic frame from anufryieu/tesla-tpms-ble's PROTOCOL.md, no real awake frame has been
        // published yet. Status 0x0A, 0x0186 = 390 kPa absolute, 0x48 = 72, 0x0BEA = 3050 mV
        decode("01020A860148EA0B")
            .let { assertNotNull(it) }
            .asTyre()
            .let { assertIs<Tyre.Unlocated>(it) }
            .also { assertEquals(290f.kpa, it.pressure) }
            .also { assertEquals(22f.celsius, it.temperature) }
            .also { assertEquals(31.toUShort(), it.battery) }
    }

    @Test
    fun `real sleep frame is ignored`() {
        // Captured from a fitted Autel MX-Sensor BLE: 020106 06FF2B02 01FE03
        assertNull(decode("01FE03"))
    }

    @Test
    fun `full length frame with a sleep status is ignored`() {
        assertNull(decode("01FE03860148EA0B"))
    }

    @Test
    fun `payload with an implausible battery voltage is ignored`() {
        // Same shape as a sensor frame but 0xFFFF mV, which is what a Tesla vehicle payload could
        // look like
        assertNull(decode("01020A860148FFFF"))
    }

    @Test
    fun `payload from another company id is ignored`() {
        assertNull(decode("01020A860148EA0B", manufacturerId = 0x00BF))
    }

    @Test
    fun `pressure below the atmospheric pressure is clamped to zero`() {
        // 0x0062 = 98 kPa absolute, an unmounted sensor on a low pressure weather day
        decode("01020A620048EA0B")
            .let { assertNotNull(it) }
            .also { assertEquals(0f.kpa, it.pressure()) }
    }

    @Test
    fun `sensor id is derived from the mac address`() {
        assertNotEquals(
            decode("01020A860148EA0B", address = "BC:6A:29:00:00:01")?.id(),
            decode("01020A860148EA0B", address = "BC:6A:29:00:00:02")?.id(),
        )
    }
}
