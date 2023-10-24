package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Sensor(val id: Int, val location: SensorLocation) : Parcelable
