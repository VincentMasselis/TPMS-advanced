package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class TyreLocationQualifier(val location: SensorLocation)
