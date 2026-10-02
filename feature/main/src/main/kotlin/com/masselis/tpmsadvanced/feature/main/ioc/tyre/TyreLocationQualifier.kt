package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.vehicle.model.Location
import dev.zacsweers.metro.Qualifier

@Qualifier
internal annotation class WheelLocationQualifier(val location: Location.Wheel)

@Qualifier
internal annotation class AxleQualifier(val axle: Location.Axle)

@Qualifier
internal annotation class SideQualifier(val side: Location.Side)
