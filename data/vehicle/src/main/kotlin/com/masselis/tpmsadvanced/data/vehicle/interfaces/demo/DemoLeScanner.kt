package com.masselis.tpmsadvanced.data.vehicle.interfaces.demo

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Suppress("MagicNumber")
public class DemoLeScanner : BluetoothLeScanner {

    private val frontLeft = listOf<ScannerRecord>(
        Tyre.Located(
            now(),
            -20,
            1,
            0.4f.bar,
            15f.celsius,
            100u,
            false,
            Location.Wheel.FRONT_LEFT,
        ),
        Tyre.Unlocated(
            now(),
            -20,
            2,
            0.4f.bar,
            15f.celsius,
            100u,
            false,
        )
    )

    private val frontRight = listOf<ScannerRecord>(
        Tyre.Located(
            now(),
            -20,
            3,
            1.6f.bar,
            20f.celsius,
            75u,
            false,
            Location.Wheel.FRONT_RIGHT,
        ),
        Tyre.Unlocated(
            now(),
            -20,
            4,
            1.6f.bar,
            20f.celsius,
            75u,
            false,
        )
    )

    private val rearLeft = listOf<ScannerRecord>(
        Tyre.Located(
            now(),
            -20,
            5,
            2.0f.bar,
            35f.celsius,
            50u,
            false,
            Location.Wheel.REAR_LEFT,
        ),
        Tyre.Unlocated(
            now(),
            -20,
            6,
            2.0f.bar,
            35f.celsius,
            50u,
            false,
        ),
    )

    private val rearRight = listOf<ScannerRecord>(
        Tyre.Located(
            now(),
            -20,
            7,
            2.8f.bar,
            95f.celsius,
            25u,
            false,
            Location.Wheel.REAR_RIGHT,
        ),
        Tyre.Unlocated(
            now(),
            -20,
            8,
            2.8f.bar,
            95f.celsius,
            25u,
            false,
        )
    )

    private val source = flow {
        (frontLeft + frontRight + rearLeft + rearRight).forEach {
            emit(it)
        }
        awaitCancellation()
    }

    override fun highDutyScan(): Flow<ScannerRecord> = source

    override fun normalScan(): Flow<ScannerRecord> = source

    override fun missingPermission(): List<String> = emptyList()

    override val isBluetoothRequired: Boolean = false
}
