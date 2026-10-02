package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

public sealed interface Tyre : ScannerRecord, Parcelable {
    public val pressure: Pressure
    public val temperature: Temperature
    public val battery: UShort
    public val isAlarm: Boolean

    @Parcelize
    public data class Unlocated(
        override val timestamp: Double,
        override val rssi: Int,
        override val sensorId: Int,
        override val pressure: Pressure,
        override val temperature: Temperature,
        override val battery: UShort,
        override val isAlarm: Boolean
    ) : Tyre

    @Parcelize
    public data class Located(
        override val timestamp: Double,
        override val rssi: Int,
        override val sensorId: Int,
        override val pressure: Pressure,
        override val temperature: Temperature,
        override val battery: UShort,
        override val isAlarm: Boolean,
        val location: Location,
    ) : Tyre {
        public constructor(source: Tyre, location: Location) : this(
            source.timestamp,
            source.rssi,
            source.sensorId,
            source.pressure,
            source.temperature,
            source.battery,
            source.isAlarm,
            location
        )
    }
}
