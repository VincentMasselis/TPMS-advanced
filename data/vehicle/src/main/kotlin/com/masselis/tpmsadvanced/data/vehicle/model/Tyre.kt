package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Tyre(
    val timestamp: Double,
    val rssi: Int,
    val location: SensorLocation?,
    val id: Int,
    val pressure: Pressure,
    val temperature: Temperature,
    val battery: UShort,
    val isAlarm: Boolean
) : Parcelable
