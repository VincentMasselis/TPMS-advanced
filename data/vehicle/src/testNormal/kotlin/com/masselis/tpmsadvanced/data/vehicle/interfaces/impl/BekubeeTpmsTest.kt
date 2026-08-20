package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class BekubeeTpmsTest {

    // 0x08: Shortened Local Name ("TPMS")
    // 0xFF: Manufacturer Specific Data, company ID 0x0002 (values below are the payload
    // *after* the company ID, i.e. what ScanRecord.getManufacturerSpecificData(0x0002) returns)
    // 0x03: Complete List of 16-bit Service Class UUIDs (0xA827)

    private val samples = listOf(
        // Full raw advertisement captured live from a physical Bekubee "TPMS" sensor:
        // 050854504D530CFF0200AF4500A7002D56A8B4030327A8
        // (name "TPMS" + manufacturer data, company ID 0002, payload below + service UUID A827,
        // all in the same packet)
        // Unlocated(sensorId=5647616, pressure=Pressure(kpa=67.0), temperature=Temperature(celsius=19.0), battery=30, isAlarm=false)
        "AF4500A7002D56A8B4",

        // From PR #445 (https://github.com/VincentMasselis/TPMS-advanced/pull/445), same
        // physical sensor (printed/QR ID 002D56).
        // Unlocated(sensorId=5647616, pressure=Pressure(kpa=37.0), temperature=Temperature(celsius=18.0), battery=30, isAlarm=false)
        "AF440089002D56087B",
        // Temperature change without a pressure change.
        // Unlocated(sensorId=5647616, pressure=Pressure(kpa=37.0), temperature=Temperature(celsius=27.0), battery=30, isAlarm=false)
        "B14D0089002D563A76",
    )

    @Test
    fun realValue() {
        samples
            .map { it.hexToByteArray() }
            .mapNotNull { manufacturerData ->
                RawBekubeeTpms(
                    mockScanResult(
                        mockScanRecord = mockScanRecord(
                            mockDeviceName = "TPMS",
                            containsServiceUuids = true,
                            mockManufacturerData = manufacturerData
                        )
                    )
                )
            }
            .map { it.asTyre() }
            .onEach(::println)
            .also { assert(it.size == samples.size) }
    }
}
