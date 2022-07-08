package com.masselis.tpmsadvanced.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SensorIds(
    val frontLeft: Int,
    val frontRight: Int,
    val rearLeft: Int,
    val rearRight: Int
) : Parcelable