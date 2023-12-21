package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class PechamTest {

    // 0x03: Complete List of 16-bit Service Class UUIDs
    // 0x08: Shortened Local Name
    // 0xFF: Proprietary data

    private val samples = listOf(
        // D8:42:00:00:E7:68 2C R.L.
        // Tyre(timestamp=1.701683789368E9, location=REAR_LEFT, id=0, pressure=Pressure(kpa=426.111), temperature=Temperature(celsius=15.0), battery=27, isAlarm=false)
        "0303A5270308425208FF101B0F026AB017",
        // Tyre(timestamp=1.701684116754E9, location=REAR_LEFT, id=0, pressure=Pressure(kpa=382.6725), temperature=Temperature(celsius=18.0), battery=28, isAlarm=false)
        "0303A5270308425208FF801C12022B5593",
        // Tyre(timestamp=1.701687243613E9, location=REAR_LEFT, id=0, pressure=Pressure(kpa=385.4305), temperature=Temperature(celsius=16.0), battery=29, isAlarm=false)
        "0303A5270308425208FF101D10022F4C32",

        // D8:41:00:00:74:5B 0A F.L.
        // Tyre(timestamp=1.701687415445E9, location=FRONT_LEFT, id=0, pressure=Pressure(kpa=363.36652), temperature=Temperature(celsius=16.0), battery=27, isAlarm=false)
        "0303A5270308425208FF801B10020F940F",

        // D8:3A:00:00:44:61 1B F.R.
        // Tyre(timestamp=1.701687787676E9, location=FRONT_RIGHT, id=0, pressure=Pressure(kpa=344.75), temperature=Temperature(celsius=19.0), battery=27, isAlarm=false)
        "0303A5270308425208FF801B1301F48D18",

        // D8:51:00:00:AE:49 3D R.R.
        // Tyre(timestamp=1.70168790933E9, location=REAR_RIGHT, id=0, pressure=Pressure(kpa=100.667), temperature=Temperature(celsius=20.0), battery=28, isAlarm=false)
        "0303A5270308425208FF401C1400921A7F",
    )

    // byteArrayEleven= 0308425208FF101B0F026A
    // Check name == "BR"

    @Test
    fun realValue() {
        samples
            .map { it.hexToByteArray() }
            .mapNotNull { completeData ->
                RawPecham(
                    mockk {
                        every { scanRecord } returns mockk {
                            every { deviceName } returns "BR"
                            every { rssi } returns -60
                            every { bytes } returns completeData
                            every { device } returns mockk {
                                every { address } returns "00:00:00:00:00"
                            }
                        }
                    },
                )
            }
            .map { it.asTyre() }
            .onEach(::println)
            .also { assert(it.size == samples.size) }
    }
}