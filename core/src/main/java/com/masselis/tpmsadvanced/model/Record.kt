package com.masselis.tpmsadvanced.model

import android.bluetooth.le.ScanResult
import androidx.core.util.size
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

data class Record(private val bytes: ByteArray) {

    val timestamp = System.currentTimeMillis().div(1000.0)

    fun location() = bytes[0].toUByte()
    fun address() = bytes.copyOfRange(1, 3)
    fun id() = bytes.copyOfRange(3, 6)
    fun pressure() = bytes.copyOfRange(6, 10)
    fun temperature() = bytes.copyOfRange(10, 14)
    fun battery() = bytes[14]
    fun alarm() = bytes[15]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (!bytes.contentEquals(other.bytes)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        fun Flow<ScanResult>.asRawRecord() = this
            .mapNotNull { result -> result.scanRecord?.manufacturerSpecificData?.takeIf { it.size > 0 } }
            .map { Record(it.valueAt(0)) }
            .filter { it.address().contentEquals(expectedAddress) }

        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()
    }
}