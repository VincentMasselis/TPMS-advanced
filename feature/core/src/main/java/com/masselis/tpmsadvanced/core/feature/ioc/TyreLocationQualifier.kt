package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.*

@Qualifier
@Retention(RUNTIME)
internal annotation class TyreLocationQualifier(val location: SensorLocation)

@Qualifier
@Retention(RUNTIME)
internal annotation class TyreAxleQualifier(val axle: SensorLocation.Axle)

@Qualifier
@Retention(RUNTIME)
internal annotation class TyreSideQualifier(val side: SensorLocation.Side)