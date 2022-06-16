package com.masselis.tpmsadvanced.model

import javax.inject.Qualifier as DaggerQualifier

enum class TyreLocation {
    FRONT_LEFT,
    FRONT_RIGHT,
    REAR_LEFT,
    REAR_RIGHT;

    @DaggerQualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Qualifier(val location: TyreLocation)
}