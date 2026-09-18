package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanRecord
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.utils.mockScanResult
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class PechamTest {

    // 0x03: Complete List of 16-bit Service Class UUIDs
    // 0x08: Shortened Local Name
    // 0xFF: Proprietary data

    private val samples = listOf(
        // D8:42:00:00:E7:68 2C R.L.
        // Unlocated(timestamp=1.760542739965E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=326.13348), temperature=Temperature(celsius=15.0), battery=27, isAlarm=false)
        "0303A5270308425208FF101B0F026AB017",
        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=282.695), temperature=Temperature(celsius=18.0), battery=28, isAlarm=false)
        "0303A5270308425208FF801C12022B5593",
        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=285.453), temperature=Temperature(celsius=16.0), battery=29, isAlarm=false)
        "0303A5270308425208FF101D10022F4C32",

        // D8:41:00:00:74:5B 0A F.L.
        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=263.389), temperature=Temperature(celsius=16.0), battery=27, isAlarm=false)
        "0303A5270308425208FF801B10020F940F",

        // D8:3A:00:00:44:61 1B F.R.
        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=244.7725), temperature=Temperature(celsius=19.0), battery=27, isAlarm=false)
        "0303A5270308425208FF801B1301F48D18",

        // D8:51:00:00:AE:49 3D R.R.
        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=0.68950003), temperature=Temperature(celsius=20.0), battery=28, isAlarm=false)
        "0303A5270308425208FF401C1400921A7F",

        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=222.7085), temperature=Temperature(celsius=20.0), battery=29, isAlarm=false)
        "0303A5270308425208FF241D1401D4BFEC",

        // Unlocated(timestamp=1.760542739968E9, rssi=-60, sensorId=-341438080, pressure=Pressure(kpa=78.603), temperature=Temperature(celsius=26.0), battery=32, isAlarm=false)
        "0303a5270308425208ff28201a01032fec",
    )

    // byteArrayEleven= 0308425208FF101B0F026A
    // Check name == "BR"

    @Test
    fun realValue() {
        samples
            .map { it.hexToByteArray() }
            .mapNotNull { completeData ->
                RawPecham(
                    mockScanResult(
                        mockScanRecord = mockScanRecord(
                            mockDeviceName = "BR",
                            mockBytes = completeData,
                        )
                    )
                )
            }
            .map { it.asTyre() }
            .onEach(::println)
            .also { assert(it.size == samples.size) }
    }
}