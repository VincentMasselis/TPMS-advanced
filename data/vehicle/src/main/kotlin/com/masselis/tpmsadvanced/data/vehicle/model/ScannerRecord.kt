package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

public sealed interface ScannerRecord : Parcelable {
    public val timestamp: Double
    public val rssi: Int
    public val sensorId: Int

    @Parcelize
    public data class Impl(
        override val timestamp: Double,
        override val rssi: Int,
        override val sensorId: Int,
    ) : ScannerRecord
}
