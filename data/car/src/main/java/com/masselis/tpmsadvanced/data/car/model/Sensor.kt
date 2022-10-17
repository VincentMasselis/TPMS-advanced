package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Sensor(val id: Int, val location: SensorLocation) : Parcelable
