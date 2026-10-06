package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.frame
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.unframe
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.GenealogyRequest.GENEALOGYREQUEST_TPWHEELUNITINFO_READ
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.TPDataRequest.TP_DATAREQUEST_PRESSURE_TEMPERATURE
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.ToTPWheelUnitMessage
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Frames are a 2 bytes big-endian size followed by the protobuf message
@OptIn(ExperimentalStdlibApi::class)
internal class TeslaGattTest {

    @Test
    fun `pressure and temperature request is framed`() {
        // Field 35 (varint) = 1
        assertEquals(
            "0003980201",
            ToTPWheelUnitMessage(tpDataRequest = TP_DATAREQUEST_PRESSURE_TEMPERATURE).frame().toHexString()
        )
    }

    @Test
    fun `wheel unit info request is framed`() {
        // Field 31 (varint) = 3
        assertEquals(
            "0003f80103",
            ToTPWheelUnitMessage(genealogyRequest = GENEALOGYREQUEST_TPWHEELUNITINFO_READ).frame().toHexString()
        )
    }

    @Test
    fun `tp data reply is decoded`() {
        // unsignedMessage (2) { TPData (28) { pressure = 101, temperature = zigzag(22) } }, values from the
        // Synacktiv Hexacon 2024 slides
        "00091207e201040865102c"
            .hexToByteArray()
            .unframe()
            ?.unsignedMessage
            ?.tpData
            .also { assertEquals(101, it?.pressure) }
            .also { assertEquals(22, it?.temperature) }
            .also { assertEquals(1f.kpa, it?.gaugePressure()) }
    }

    @Test
    fun `negative temperature is decoded`() {
        // temperature = zigzag(-10) = 19
        "00091207e2010408651013"
            .hexToByteArray()
            .unframe()
            .also { assertEquals(-10, it?.unsignedMessage?.tpData?.temperature) }
    }

    @Test
    fun `wheel unit info reply is decoded and its unknown fields are skipped`() {
        // unsignedMessage (2) { TPWheelUnitInfo (29) { TIAppCRC (1) = ABCD, batteryVoltage_mV (3) = 3011 } }
        "000c120aea01070a02abcd18c317"
            .hexToByteArray()
            .unframe()
            .also { assertEquals(3011, it?.unsignedMessage?.tpWheelUnitInfo?.batteryVoltageMv) }
    }

    @Test
    fun `gauge pressure is clamped to zero below the atmospheric pressure`() {
        assertEquals(0f.kpa, TeslaGatt.TPData(pressure = 98).gaugePressure())
    }

    @Test
    fun `reply with a wrong size prefix is ignored`() {
        assertNull("00ff1207e201040865102c".hexToByteArray().unframe())
    }

    @Test
    fun `reply too short to contain a size prefix is ignored`() {
        assertNull("00".hexToByteArray().unframe())
    }

    @Test
    fun `invalid protobuf content is ignored`() {
        assertNull("0002ffff".hexToByteArray().unframe())
    }
}
