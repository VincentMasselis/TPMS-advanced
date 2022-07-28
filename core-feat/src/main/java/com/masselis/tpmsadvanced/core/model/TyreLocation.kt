package com.masselis.tpmsadvanced.core.model

import com.masselis.tpmsadvanced.core.interfaces.coreComponent
import com.masselis.tpmsadvanced.core.ioc.TyreComponent
import javax.inject.Qualifier as DaggerQualifier

enum class TyreLocation(
    val byte: UByte
) {
    FRONT_LEFT(0x80u),
    FRONT_RIGHT(0x81u),
    REAR_LEFT(0x82u),
    REAR_RIGHT(0x83u);

    val component: TyreComponent by lazy { coreComponent.findTyreComponentUseCase.find(this) }

    @DaggerQualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Qualifier(val location: TyreLocation)
}