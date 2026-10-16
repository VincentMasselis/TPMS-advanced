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
        override val isAlarm: Boolean,
        override val infos: Set<ManufacturerSpecificInfo> = emptySet()
    ) : Tyre {
        public constructor(
            timestamp: Double,
            rssi: Int,
            sensorId: Int,
            pressure: Pressure,
            temperature: Temperature,
            battery: UShort,
            isAlarm: Boolean,
            vararg infos: ManufacturerSpecificInfo
        ) : this(timestamp, rssi, sensorId, pressure, temperature, battery, isAlarm, infos.toSet())
    }

    @Parcelize
    public data class Located(
        override val timestamp: Double,
        override val rssi: Int,
        override val sensorId: Int,
        override val pressure: Pressure,
        override val temperature: Temperature,
        override val battery: UShort,
        override val isAlarm: Boolean,
        override val infos: Set<ManufacturerSpecificInfo> = emptySet(),
        val location: Location,
    ) : Tyre {

        public constructor(
            timestamp: Double,
            rssi: Int,
            sensorId: Int,
            pressure: Pressure,
            temperature: Temperature,
            battery: UShort,
            isAlarm: Boolean,
            location: Location,
            vararg infos: ManufacturerSpecificInfo
        ) : this(
            timestamp,
            rssi,
            sensorId,
            pressure,
            temperature,
            battery,
            isAlarm,
            infos.toSet(),
            location
        )

        public constructor(source: Tyre, location: Location) : this(
            source.timestamp,
            source.rssi,
            source.sensorId,
            source.pressure,
            source.temperature,
            source.battery,
            source.isAlarm,
            source.infos,
            location
        )
    }
}
