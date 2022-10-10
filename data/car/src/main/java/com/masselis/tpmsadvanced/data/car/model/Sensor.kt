package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Sensor(val id: Int, val location: TyreLocation) : Parcelable
