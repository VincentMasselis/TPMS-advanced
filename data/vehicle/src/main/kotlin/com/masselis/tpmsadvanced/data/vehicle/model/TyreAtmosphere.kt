package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class TyreAtmosphere(
    val timestamp: Double,
    val pressure: Pressure,
    val temperature: Temperature
) : Parcelable
