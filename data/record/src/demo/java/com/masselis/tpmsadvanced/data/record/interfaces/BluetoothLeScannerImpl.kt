package com.masselis.tpmsadvanced.data.record.interfaces

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordComponent
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@DataRecordComponent.Scope
internal class BluetoothLeScannerImpl @Inject constructor() : BluetoothLeScanner {

    private val frontLeft = Tyre(
        now(),
        SensorLocation.FRONT_LEFT,
        SensorLocation.FRONT_LEFT.ordinal,
        0.4f.bar,
        15f.celsius,
        100u,
        false
    )

    private val frontRight = Tyre(
        now(),
        SensorLocation.FRONT_RIGHT,
        SensorLocation.FRONT_RIGHT.ordinal,
        1.6f.bar,
        20f.celsius,
        75u,
        false
    )

    private val rearLeft = Tyre(
        now(),
        SensorLocation.REAR_LEFT,
        SensorLocation.REAR_LEFT.ordinal,
        2.0f.bar,
        35f.celsius,
        50u,
        false
    )

    private val rearRight = Tyre(
        now(),
        SensorLocation.REAR_RIGHT,
        SensorLocation.REAR_RIGHT.ordinal,
        2.8f.bar,
        95f.celsius,
        25u,
        false
    )

    private val source = channelFlow {
        listOf(frontLeft, frontRight, rearLeft, rearRight).forEach {
            send(it)
        }
        awaitClose { }
    }

    override fun highDutyScan(): Flow<Tyre> = source

    override fun normalScan(): Flow<Tyre> = source

    override fun missingPermission(): List<String> = emptyList()

    override fun isChipTurnedOn(): Flow<Boolean> = flowOf(true)
}
