package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalStdlibApi::class)
@Suppress("DataClassPrivateConstructor")
internal data class RawPecham private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val data: ByteArray
) : Raw {

    fun id() = macAddress.hashCode()
    fun pressure() = data
        .copyOfRange(3, 5)
        .let {
            ByteBuffer.wrap(it)
                .order(ByteOrder.BIG_ENDIAN)
                .getShort()
        }
        .div(10f)
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

        other as RawPecham

        if (macAddress != other.macAddress) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        operator fun invoke(result: ScanResult): RawPecham? {
            val scanRecord = result.scanRecord
                ?: return null
            if (scanRecord.deviceName != "BR")
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
            return RawPecham(result.device.address, result.rssi, data)
        }

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

            // Reverse engineered with the help of ChatGPT
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
                -115,
                113,
                -89,
                0,
                -43,
                118,
                54,
                -74,
                108,
                126,
                94,
                -15,
                86,
                91,
                -123,
                -75,
                -72,
                -92,
                1,
                -59,
                53,
                -20,
                -90,
                10,
                -22,
                107,
                117,
                -90,
                79,
                -106,
                82,
                -57,
                -73,
                43,
                -103,
                -84,
                -43,
                -36,
                -66,
                66,
                39,
                -29,
                -107,
                -22,
                -72,
                -50,
                -75,
                -93,
                -106,
                -124,
                114,
                127,
                64,
                111,
                -110,
                -78,
                -118,
                -85,
                -108,
                54,
                45,
                111,
                -80,
                123,
                38,
                -66,
                -110,
                -122,
                -32,
                -123,
                -64,
                66,
                22,
                -75,
                -40,
                -22,
                109,
                -11,
                -38,
                -108,
                117,
                -7,
                43,
                -105,
                15,
                23,
                85,
                97,
                -94,
                89,
                64,
                94,
                47,
                -76,
                116,
                -119,
                115,
                15,
                -41,
                15,
                -41,
                -18,
                56,
                36,
                87,
                75,
                -57,
                -31,
                47,
                -25,
                -125,
                -112,
                -28,
                -83,
                -74,
                6,
                -100,
                77,
                127,
                -105,
                -65,
                52,
                48,
                26,
                48,
                -24,
                -64,
                -15,
                -48,
                -80,
                -88,
                69,
                53,
                -37,
                31,
                -87,
                91,
                -63,
                70,
                -98,
                106,
                -44,
                15,
                8,
                119,
                -84,
                100,
                -28,
                99,
                -29,
                -44,
                62,
                8,
                105,
                -116,
                -51,
                46,
                -8,
                -9,
                122,
                -8,
                101,
                26,
                -107,
                -90,
                77,
                122,
                -17,
                111,
                121,
                40,
                18,
                -19,
                44,
                -108,
                58,
                27,
                -112,
                -115,
                -72,
                124,
                -25,
                9,
                -102,
                -44,
                -124,
                97,
                99,
                -78,
                28,
                -19,
                -60,
                92,
                -33,
                10,
                -25,
                1,
                53,
                87,
                -77,
                3,
                -127,
                52,
                97,
                6,
                -6,
                6,
                -83,
                45,
                -26,
                83,
                -102,
                111,
                -106,
                25,
                48,
                -92,
                -64,
                73,
                24,
                -85,
                104,
                -110,
                -50,
                94,
                -87,
                2,
                -79,
                54,
                20,
                -45,
                -86,
                -52,
                7,
                -83,
                120,
                5,
                -84,
                -57,
                21,
                127,
                -92,
                -33,
                10,
                -32,
                8,
                76,
                -53,
                -93,
                76,
                99,
                32,
                -115,
                -64,
                -102,
                59
            )
            private val auchCRCLo = byteArrayOf(
                32,
                -64,
                -63,
                17,
                -61,
                3,
                2,
                -62,
                -58,
                126,
                127,
                -65,
                125,
                -67,
                -68,
                124,
                -52,
                12,
                13,
                -51,
                15,
                -49,
                -50,
                14,
                10,
                -54,
                -53,
                11,
                -55,
                9,
                8,
                -56,
                -100,
                92,
                93,
                -99,
                95,
                -97,
                -98,
                94,
                90,
                -102,
                -101,
                91,
                -103,
                89,
                88,
                -104,
                -40,
                24,
                25,
                -39,
                27,
                -37,
                -38,
                26,
                30,
                -34,
                -33,
                31,
                -35,
                29,
                28,
                -36,
                20,
                -44,
                -43,
                21,
                -41,
                23,
                22,
                -42,
                -46,
                18,
                19,
                -45,
                17,
                -47,
                -48,
                16,
                -16,
                48,
                49,
                -15,
                51,
                -13,
                -14,
                50,
                54,
                -10,
                -9,
                55,
                -11,
                53,
                52,
                -12,
                60,
                -4,
                -3,
                61,
                -1,
                63,
                62,
                -2,
                -6,
                58,
                59,
                -5,
                57,
                -7,
                -8,
                56,
                40,
                -24,
                -23,
                41,
                -21,
                43,
                42,
                -22,
                -18,
                46,
                47,
                -17,
                45,
                -19,
                -20,
                44,
                -28,
                36,
                37,
                -27,
                39,
                -25,
                -26,
                38,
                34,
                -30,
                -29,
                35,
                -31,
                33,
                32,
                -32,
                -120,
                72,
                73,
                -119,
                75,
                -117,
                -118,
                74,
                78,
                -114,
                -113,
                79,
                -115,
                77,
                76,
                -116,
                -96,
                96,
                97,
                -95,
                99,
                -93,
                -94,
                98,
                102,
                -90,
                -89,
                103,
                -91,
                101,
                100,
                -92,
                108,
                -84,
                -83,
                109,
                -81,
                111,
                110,
                -82,
                -86,
                106,
                107,
                -85,
                105,
                -87,
                -88,
                104,
                120,
                -72,
                -71,
                121,
                -69,
                123,
                122,
                -70,
                -66,
                126,
                127,
                -58,
                6,
                7,
                -57,
                5,
                -76,
                116,
                117,
                -75,
                119,
                -73,
                -74,
                118,
                114,
                -78,
                -77,
                115,
                -79,
                113,
                112,
                -80,
                80,
                -112,
                -109,
                81,
                -109,
                83,
                82,
                -110,
                -106,
                86,
                87,
                -105,
                85,
                -107,
                -108,
                84,
                68,
                -124,
                -123,
                69,
                -121,
                71,
                70,
                -122,
                -126,
                66,
                67,
                -125,
                65,
                -127,
                -128,
                64
            )
        }
    }
}