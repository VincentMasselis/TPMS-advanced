package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import io.mockk.every
import io.mockk.mockk
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
internal class A827Test {

    private val samples = listOf(
        // Sensor 002D56, approximately 5.4 PSI, approximately 18°C
        "050854504D530CFF0200AF440089002D56087B030327A8",

        // Same sensor and pressure after warming by hand, approximately 27°C
        "050854504D530CFF0600B14D0089002D563A76030327A8",
    )

    @Test
    fun realValue() {
        samples
            .map { it.hexToByteArray() }
            .mapNotNull { completeData ->
                RawA827(
                    mockk {
                        every { rssi } returns -60
                        every { scanRecord } returns mockk {
                            every { serviceUuids } returns mockk {
                                every { contains(any()) } returns true
                            }
                            every { bytes } returns completeData
                        }
                    },
                )
            }
            .map { it.asTyre() }
            .onEach(::println)
            .also { assert(it.size == samples.size) }
    }
}