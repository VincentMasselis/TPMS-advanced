package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import io.mockk.every
import io.mockk.mockk
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class WircarlinkTest {

    // 0x03: Complete List of 16-bit Service Class UUIDs
    // 0x08: Shortened Local Name
    // 0xFF: Proprietary data

    private val samples = listOf(
        // From https://github.com/VincentMasselis/TPMS-advanced/issues/428
        // Unlocated(timestamp=1.78653863791E9, rssi=-60, sensorId=402095, pressure=Pressure(kpa=210.64801), temperature=Temperature(celsius=27.0), battery=33, isAlarm=false)
        "0201060303B0FB12FFAC00D043520008DA001D10FF1100AF2206",
    )
    @Test
    fun realValue() {
        samples
            .map { it.hexToByteArray() }
            .mapNotNull { completeData ->
                RawWicarlink(
                    mockk {
                        every { scanRecord } returns mockk {
                            every { rssi } returns -60
                            every { advertiseFlags } returns 0x06
                            every { serviceUuids } returns mockk {
                                every { contains(any()) } returns true
                            }
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