package com.masselis.tpmsadvanced.data.car.model

import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import java.util.*

public data class Car(
    public val uuid: UUID,
    public val name: String,
    public val isFavourite: Boolean,
    public val lowPressure: Pressure,
    public val highPressure: Pressure,
    public val lowTemp: Temperature,
    public val normalTemp: Temperature,
    public val highTemp: Temperature,
)
