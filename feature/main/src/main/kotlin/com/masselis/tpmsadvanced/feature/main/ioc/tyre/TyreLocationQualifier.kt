package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import dev.zacsweers.metro.Qualifier

@Qualifier
internal annotation class WheelLocationQualifier(val location: SensorLocation)

@Qualifier
internal annotation class AxleQualifier(val axle: SensorLocation.Axle)

@Qualifier
internal annotation class SideQualifier(val side: SensorLocation.Side)
