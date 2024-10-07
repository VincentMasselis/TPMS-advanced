package com.masselis.tpmsadvanced.data.vehicle.interfaces

import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow

public interface BluetoothLeScanner {

    public sealed interface Failure {
        public class ScannerIsNull(adapterState: Int?) :
            Exception("Failure.ScannerIsNull(adapterState=$adapterState)")

        public class Scan(reason: Int) : Exception("Failure.Scan(reason=$reason)")
    }

    public fun highDutyScan(): Flow<Tyre.SensorInput>
    public fun normalScan(): Flow<Tyre.SensorInput>

    public fun missingPermission(): List<String>

    public val isBluetoothRequired: Boolean
}
