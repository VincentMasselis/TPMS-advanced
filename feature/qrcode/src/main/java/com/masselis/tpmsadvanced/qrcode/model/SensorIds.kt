package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_RIGHT
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SensorIds(
    val frontLeft: Int,
    val frontRight: Int,
    val rearLeft: Int,
    val rearRight: Int
) : Parcelable, Map<TyreLocation, Int> by mapOf(
    FRONT_LEFT to frontLeft,
    FRONT_RIGHT to frontRight,
    REAR_LEFT to rearLeft,
    REAR_RIGHT to rearRight
)
