package com.masselis.tpmsadvanced.data.record.interfaces

import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.flow.Flow

public interface BluetoothLeScanner {
    public class ScanFailed(reason: Int) : Exception("ScanFailed(reason=$reason)")

    public fun highDutyScan(): Flow<Tyre>
    public fun normalScan(): Flow<Tyre>

    public fun missingPermission(): List<String>
    public fun isChipTurnedOn(): Flow<Boolean>
}
