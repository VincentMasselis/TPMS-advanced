package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class BluetoothLeScannerImpl : BluetoothLeScanner {

    private val frontLeft = listOf(
        Tyre.SensorLocated(
            now(),
            -20,
            1,
            0.4f.bar,
            15f.celsius,
            100u,
            false,
            SensorLocation.FRONT_LEFT,
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

    private val frontRight = listOf(
        Tyre.SensorLocated(
            now(),
            -20,
            3,
            1.6f.bar,
            20f.celsius,
            75u,
            false,
            SensorLocation.FRONT_RIGHT,
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

    private val rearLeft = listOf(
        Tyre.SensorLocated(
            now(),
            -20,
            5,
            2.0f.bar,
            35f.celsius,
            50u,
            false,
            SensorLocation.REAR_LEFT,
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

    private val rearRight = listOf(
        Tyre.SensorLocated(
            now(),
            -20,
            7,
            2.8f.bar,
            95f.celsius,
            25u,
            false,
            SensorLocation.REAR_RIGHT,
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

    override fun highDutyScan(): Flow<Tyre.SensorInput> = source

    override fun normalScan(): Flow<Tyre.SensorInput> = source

    override fun missingPermission(): List<String> = emptyList()

    override val isBluetoothRequired = false
}
