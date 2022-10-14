package com.masselis.tpmsadvanced.data.record.interfaces

import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.flow.Flow

public interface BluetoothLeScanner {
    @Suppress("MemberVisibilityCanBePrivate")
    public class ScanFailed(public val reason: Int) : Exception() {
        override fun toString(): String = "ScanFailed(reason=$reason)"
    }

    public fun highDutyScan(): Flow<Tyre>
    public fun normalScan(): Flow<Tyre>

    public fun missingPermission(): List<String>
    public fun isChipTurnedOn(): Flow<Boolean>
}
