package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
internal annotation class WheelLocationQualifier(val location: SensorLocation)

@Qualifier
@Retention(RUNTIME)
internal annotation class AxleQualifier(val axle: SensorLocation.Axle)

@Qualifier
@Retention(RUNTIME)
internal annotation class SideQualifier(val side: SensorLocation.Side)
