package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.decode
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Messages are bare protobuf, without any size prefix
@OptIn(ExperimentalStdlibApi::class)
internal class TeslaGattTest {

    @Test
    fun `real tp data indication is decoded`() {
        // Captured from an unmounted sensor (F8:39:F5:34:49:A5) right after enabling the indications:
        // unsignedMessage (2) { TPData (28) { pressure = 100, temperature = zigzag(29) } }
        "1207e201040864103a"
            .hexToByteArray()
            .decode()
            ?.unsignedMessage
            ?.tpData
            .also { assertEquals(100, it?.pressure) }
            .also { assertEquals(29, it?.temperature) }
            // Unmounted, the sensor measures the atmospheric pressure
            .also { assertEquals(0f.kpa, it?.gaugePressure()) }
    }

    @Test
    fun `negative temperature is decoded`() {
        // temperature = zigzag(-10) = 19
        "1207e2010408641013"
            .hexToByteArray()
            .decode()
            .also { assertEquals(-10, it?.unsignedMessage?.tpData?.temperature) }
    }

    @Test
    fun `real heartbeat is decoded`() {
        // Captured every 4 minutes from a static sensor (F8:39:F5:34:49:A5):
        // unsignedMessage (2) { ConnectionMetrics (34) { good = 222, missed = 16, badCRC = 5 } }
        "120a92020708de0110101805"
            .hexToByteArray()
            .decode()
            ?.unsignedMessage
            .also { assertNull(it?.tpData) }
            .also { assertEquals(TeslaGatt.ConnectionMetrics(222, 16, 5), it?.connectionMetrics) }
    }

    @Test
    fun `unknown messages are decoded without tp data`() {
        // unsignedMessage (2) { TPWheelUnitInfo (29) { TIAppCRC (1) = ABCD, batteryVoltage_mV (3) = 3011 } }
        "120aea01070a02abcd18c317"
            .hexToByteArray()
            .decode()
            .also { assertNull(it?.unsignedMessage?.tpData) }
    }

    @Test
    fun `gauge pressure is the absolute pressure minus one standard atmosphere`() {
        assertEquals(288.675f, TeslaGatt.TPData(pressure = 390).gaugePressure().kpa, 0.001f)
    }

    @Test
    fun `gauge pressure is clamped to zero below the atmospheric pressure`() {
        assertEquals(0f.kpa, TeslaGatt.TPData(pressure = 98).gaugePressure())
    }

    @Test
    fun `invalid protobuf content is ignored`() {
        assertNull("ffff".hexToByteArray().decode())
    }
}
