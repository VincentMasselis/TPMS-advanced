package com.masselis.tpmsadvanced.data.vehicle.interfaces

import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow

public interface BluetoothLeScanner {
    public class ScanFailed(reason: Int) : Exception("ScanFailed(reason=$reason)")

    public fun highDutyScan(): Flow<Tyre.SensorInput>
    public fun normalScan(): Flow<Tyre.SensorInput>

    public fun missingPermission(): List<String>

    public val isBluetoothRequired: Boolean
}
