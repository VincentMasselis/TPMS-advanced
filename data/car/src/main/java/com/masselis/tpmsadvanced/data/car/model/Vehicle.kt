package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
public data class Vehicle(
    public val uuid: UUID,
    public val name: String,
    public val lowPressure: Pressure,
    public val highPressure: Pressure,
    public val lowTemp: Temperature,
    public val normalTemp: Temperature,
    public val highTemp: Temperature,
) : Parcelable
