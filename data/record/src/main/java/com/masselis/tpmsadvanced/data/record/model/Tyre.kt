package com.masselis.tpmsadvanced.data.record.model

public data class Tyre(
    val timestamp: Double,
    val location: TyreLocation,
    val id: Int,
    val pressure: Pressure,
    val temperature: Temperature,
    val battery: UShort,
    val isAlarm: Boolean
)
