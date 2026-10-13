package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

public sealed interface ManufacturerSpecificInfo : Parcelable

@JvmInline
@Parcelize
public value class SuspiciousTeslaFixedTemperature(
    public val temp: Temperature
) : ManufacturerSpecificInfo
