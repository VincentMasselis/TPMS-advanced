package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.util.UUID.fromString

/**
 * Copied from [RawPecham], the only changes are name filtering ("KY" instead of "BR") and the CRC
 * tables
 */
@OptIn(ExperimentalStdlibApi::class)
@ConsistentCopyVisibility
internal data class RawBekubeeKy private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val data: ByteArray
) : Raw {

    fun id() = macAddress.hashCode()

    fun pressure() = (((data[3].toInt() and 0xFF) shl 8) or (data[4].toInt() and 0xFF))
        .minus(146)
        .div(10)
        .toFloat()
        .psi

    fun battery() = data[1].toUShort() // Returns 27 for 2.7 volts

    fun temperature() = data[2].toFloat().celsius

    override fun asTyre() = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        battery(),
        battery() < 26u // Mimics the alarm from the official app
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawBekubeeKy

        if (macAddress != other.macAddress) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        internal val SERVICE_UUID = ParcelUuid(fromString("000027a5-0000-1000-8000-00805f9b34fb"))

        operator fun invoke(result: ScanResult): RawBekubeeKy? {
            val scanRecord = result.scanRecord
                ?: return null
            if (scanRecord.deviceName != "KY")
                return null
            if (CRC.isValid(scanRecord).not())
                return null
            // Calling result.scanRecord?.manufacturerSpecificData?.valueAt(0) will not work because
            // the returned array is 5 bytes only instead of 7 bytes. It doesn't contain the first 2
            // bytes
            val data = runCatching { scanRecord.bytes.copyOfRange(10, 17) }
                .onFailure {
                    Firebase.crashlytics.recordException(
                        IllegalArgumentException(
                            "Filled bytes are incorrect: $${scanRecord.bytes.toHexString()}",
                            it
                        )
                    )
                }
                .getOrNull()
                ?: return null
            return RawBekubeeKy(result.device.address, result.rssi, data)
        }

        // Reverse engineered by decompiling Bekubee's official HRTPMS Android app
        // (com.bekubee.hrtpms) with the help of Claude
        private object CRC {
            fun isValid(scanRecord: ScanRecord): Boolean {
                val dataWithCRC = scanRecord.bytes
                val (highByteIndex, lowByteIndex) = bytes(
                    dataWithCRC.take(dataWithCRC.size - 2).toByteArray()
                )
                return auchCRCHi[highByteIndex] == dataWithCRC[15] && auchCRCLo[lowByteIndex] == dataWithCRC[16]
            }

            private fun bytes(dataWithoutCRC: ByteArray): Pair<Int, Int> =
                calculate(data = dataWithoutCRC)
                    .let { (it shr 8 and 0xFF) to (it and 0xFF) }

            private fun calculate(
                polynomial: Int = 32773,
                initialValue: Int = 65535,
                data: ByteArray,
                startIndex: Int = 0,
                length: Int = 15,
                reverseData: Boolean = false,
                reverseResult: Boolean = false,
                finalXOR: Int = 0
            ): Int {
                var crcValue = initialValue
                var dataIndex = startIndex

                while (dataIndex < startIndex + length && dataIndex < data.size) {
                    val currentByte = data[dataIndex]
                    var bitOrder: Int
                    var crcTemp = crcValue

                    for (bitIndex in 0..7) {
                        bitOrder = if (reverseData) {
                            7 - bitIndex
                        } else {
                            bitIndex
                        }
                        var crcMSB = true

                        val dataBit: Boolean = currentByte.toInt() shr (7 - bitOrder) and 1 == 1
                        if (crcTemp shr 15 and 1 != 1) {
                            crcMSB = false
                        }
                        val shiftedCRC = crcTemp shl 1
                        crcTemp = shiftedCRC

                        if (dataBit xor crcMSB) {
                            crcTemp = shiftedCRC xor polynomial
                        }
                    }
                    ++dataIndex
                    crcValue = crcTemp
                }

                return if (reverseResult) Integer.reverse(crcValue) ushr 16 xor finalXOR else crcValue xor finalXOR and 65535
            }

            private val auchCRCHi = byteArrayOf(
                63,
                -39,
                -11,
                -42,
                -111,
                85,
                121,
                -92,
                75,
                -45,
                -68,
                -59,
                54,
                76,
                -71,
                -101,
                -104,
                -85,
                -35,
                52,
                113,
                123,
                1,
                -66,
                53,
                14,
                112,
                -100,
                26,
                -113,
                -36,
                102,
                -103,
                -94,
                127,
                -60,
                -44,
                -3,
                35,
                122,
                93,
                72,
                97,
                -95,
                -46,
                -15,
                3,
                16,
                3,
                -36,
                -61,
                -111,
                92,
                -56,
                -57,
                8,
                -27,
                72,
                65,
                35,
                -43,
                45,
                45,
                13,
                -8,
                -21,
                21,
                111,
                -108,
                -34,
                87,
                49,
                28,
                19,
                104,
                -21,
                109,
                109,
                119,
                -42,
                23,
                -97,
                77,
                96,
                -58,
                -44,
                -73,
                -96,
                -40,
                -37,
                20,
                -69,
                -103,
                -29,
                -126,
                39,
                26,
                -18,
                84,
                6,
                49,
                58,
                -118,
                -97,
                -116,
                44,
                -71,
                -95,
                50,
                -1,
                -48,
                -6,
                -115,
                -21,
                -84,
                29,
                -88,
                30,
                94,
                -45,
                -81,
                110,
                48,
                -105,
                45,
                27,
                57,
                -39,
                37,
                82,
                126,
                23,
                -67,
                97,
                48,
                122,
                118,
                116,
                35,
                36,
                -103,
                -20,
                -85,
                51,
                -95,
                112,
                27,
                105,
                98,
                -52,
                -90,
                96,
                -101,
                111,
                -80,
                124,
                -58,
                115,
                61,
                109,
                65,
                -95,
                50,
                86,
                -80,
                3,
                18,
                101,
                -107,
                -63,
                14,
                -101,
                -44,
                74,
                56,
                0,
                55,
                -42,
                -17,
                87,
                -9,
                63,
                118,
                85,
                95,
                75,
                74,
                -33,
                -60,
                -43,
                44,
                -63,
                66,
                58,
                -117,
                94,
                -106,
                -71,
                -113,
                -15,
                -103,
                -61,
                -47,
                -108,
                -17,
                96,
                -4,
                41,
                -48,
                48,
                -13,
                -19,
                -21,
                46,
                101,
                58,
                -109,
                -5,
                117,
                10,
                -87,
                86,
                -66,
                33,
                -79,
                -37,
                42,
                41,
                -104,
                -35,
                19,
                79,
                -125,
                -32,
                118,
                -128,
                92,
                -121,
                -97,
                -74,
                39,
                -24,
                69,
                -26,
                -124,
                32,
                -20,
                98,
                117,
                -86,
                3,
                78,
                10,
                -11,
                -74,
                52
            )
            private val auchCRCLo = byteArrayOf(
                17,
                44,
                103,
                9,
                -106,
                122,
                -95,
                -80,
                -95,
                -116,
                37,
                -14,
                13,
                78,
                -38,
                84,
                119,
                53,
                71,
                -22,
                -63,
                -120,
                -120,
                -6,
                8,
                78,
                65,
                19,
                86,
                64,
                124,
                -17,
                77,
                106,
                79,
                31,
                -74,
                -83,
                -70,
                -16,
                1,
                20,
                77,
                13,
                -106,
                -86,
                -7,
                80,
                -61,
                -73,
                92,
                -22,
                -122,
                -86,
                -70,
                94,
                91,
                9,
                102,
                0,
                78,
                -31,
                -11,
                -1,
                -32,
                -78,
                27,
                82,
                123,
                -35,
                100,
                -13,
                -80,
                -50,
                -18,
                -79,
                51,
                -112,
                -63,
                31,
                33,
                1,
                -31,
                9,
                -55,
                89,
                75,
                -85,
                -62,
                -23,
                36,
                69,
                -23,
                75,
                59,
                104,
                66,
                -96,
                -107,
                -119,
                111,
                84,
                62,
                106,
                -103,
                46,
                -15,
                -82,
                -38,
                -66,
                120,
                122,
                -42,
                55,
                -29,
                46,
                -100,
                -79,
                103,
                40,
                59,
                32,
                87,
                -68,
                -23,
                -72,
                -2,
                102,
                51,
                104,
                -49,
                -84,
                50,
                -79,
                -82,
                -92,
                -98,
                -119,
                -66,
                118,
                47,
                -93,
                75,
                90,
                44,
                5,
                107,
                114,
                22,
                -96,
                13,
                -120,
                29,
                62,
                71,
                18,
                -79,
                -49,
                -60,
                -39,
                84,
                -127,
                -126,
                66,
                87,
                -21,
                -96,
                -18,
                25,
                123,
                -92,
                125,
                57,
                -90,
                -113,
                115,
                109,
                16,
                -31,
                -42,
                23,
                -50,
                -25,
                -2,
                5,
                39,
                -101,
                41,
                -107,
                51,
                -26,
                114,
                -2,
                90,
                -89,
                123,
                91,
                8,
                34,
                119,
                -115,
                -30,
                -60,
                -82,
                48,
                -56,
                -47,
                -47,
                64,
                -104,
                94,
                -80,
                91,
                29,
                112,
                -16,
                -39,
                -126,
                57,
                27,
                -8,
                -88,
                -59,
                -75,
                -11,
                48,
                107,
                -57,
                -106,
                1,
                23,
                81,
                37,
                -88,
                -102,
                70,
                -104,
                -25,
                -127,
                91,
                13,
                -84,
                19,
                17,
                19,
                99,
                18,
                -38,
                127,
                -66,
                120,
                -128,
                36,
                -83,
                27,
                -16
            )
        }
    }
}
