package com.masselis.tpmsadvanced.usecase

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class SensorIdUseCase @Inject constructor() {
    fun asInt(source: ByteArray): Int {
        assert(source.size == 3) { "Source byte array length must be exactly 3" }
        return ByteBuffer
            .wrap(byteArrayOf(0x00) + source)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int
    }
}