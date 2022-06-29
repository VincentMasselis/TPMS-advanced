package com.masselis.tpmsadvanced.model

import com.masselis.tpmsadvanced.interfaces.mainComponent
import javax.inject.Qualifier as DaggerQualifier

enum class TyreLocation(val byte: UByte) {
    FRONT_LEFT(0x80u),
    FRONT_RIGHT(0x81u),
    REAR_LEFT(0x82u),
    REAR_RIGHT(0x83u);

    @DaggerQualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Qualifier(val location: TyreLocation)

    val component get() = mainComponent.findTyreComponentUseCase.find(this)
}