package com.masselis.tpmsadvanced.feature.qrcode.usecase

@OptIn(ExperimentalStdlibApi::class)
internal object SensorIdParser {

    private val sensorIdRegex = "^[0-9a-fA-F]{6}$".toRegex()

    fun parse(value: String): Int? {
        val normalized = value.trim()

        if (!sensorIdRegex.matches(normalized)) {
            return null
        }

        val bytes = normalized.hexToByteArray()

        return (bytes[0].toInt() and 0xFF) or
                ((bytes[1].toInt() and 0xFF) shl 8) or
                ((bytes[2].toInt() and 0xFF) shl 16)
    }
}