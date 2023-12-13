package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Sensor(
    val id: Int,
    val location: Location,
) : Parcelable