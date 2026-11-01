package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import org.junit.Test

@Suppress("MaxLineLength")
internal class DjtpmsTest {

    private val samples = listOf(
        // MAC 9C:7F:64:5B:4E:52, Unlocated(sensorId=1683705426, pressure=209.0 kPa, temperature=23.0, battery=29)
        "0302E0FF0708444A54504D530FFF00001D17013600879C7F645B4E52",
        // MAC 0C:3D:5E:4E:8B:E6, Unlocated(sensorId=1582205926, pressure=239.0 kPa, temperature=39.0, battery=31)
        "0302E0FF0708444A54504D530FFF00001F27015400060C3D5E4E8BE6",
    )

    private fun rawDjtpms(hex: String, rssi: Int = -60) = RawDjtpms(
        mockScanResult(
            mockScanRecord = mockScanRecord(
                mockDeviceName = "DJTPMS",
                containsServiceUuids = true,
                mockAdvertiseFlags = 0x06,
                mockBytes = hex.hexToByteArray()
            ),
            mockRssi = rssi
        )
    )

    @Test
    fun corruptedFrameIsRejected() {
        // Same first sample with the CRC byte flipped from 0x87 to 0x88
        assert(rawDjtpms("0302E0FF0708444A54504D530FFF00001D17013600889C7F645B4E52") == null)
    }

    @Test
    fun foreignSensorIsIgnored() {
        // "NOTTPM" as local name
        assert(rawDjtpms("0302E0FF07084E4F5454504D" + "0FFF00001D17013600879C7F645B4E52") == null)
    }

    @Test
    fun frameWithoutMacAddressIsAccepted() {
        // Same payload as the first sample, but the advertisement stops after the CRC: Android
        // does not always expose the trailing MAC bytes.
        val raw = rawDjtpms("0302E0FF0708444A54504D530DFF00001D1701360087")
        assert(raw != null)
        assert(raw!!.pressure() == 209.0f.kpa)
        assert(raw.temperature() == 23.0f.celsius)
        assert(raw.battery() == 29.toUShort())
    }

    @Test
    fun rssiDoesNotBreakDeduplication() {
        // The same measurement must be equal across advertisements, otherwise every scan
        // (each with its own rssi) would look like a new reading.
        val first = "0302E0FF0708444A54504D530FFF00001D17013600879C7F645B4E52"
        assert(rawDjtpms(first, rssi = -60) == rawDjtpms(first, rssi = -82))
    }

    @Test
    fun realValue() {
        samples
            .mapNotNull { rawDjtpms(it) }
            .map { it.asTyre() }
            .onEach(::println)
            .also { assert(it.size == samples.size) }
    }
}
