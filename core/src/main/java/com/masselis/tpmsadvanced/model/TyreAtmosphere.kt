package com.masselis.tpmsadvanced.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TyreAtmosphere(
    val timestamp: Double,
    val pressure: Pressure,
    val temperature: Temperature
) : Parcelable