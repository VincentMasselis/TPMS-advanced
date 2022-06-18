package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.ioc.TyreComponent
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.model.TyreLocation.*
import javax.inject.Inject

class FindTyreComponentUseCase @Inject constructor(
    @Qualifier(FRONT_LEFT) private val frontLeft: TyreComponent,
    @Qualifier(FRONT_RIGHT) private val frontRight: TyreComponent,
    @Qualifier(REAR_LEFT) private val rearLeft: TyreComponent,
    @Qualifier(REAR_RIGHT) private val rearRight: TyreComponent
) {
    fun find(location: TyreLocation) = when (location) {
        FRONT_LEFT -> frontLeft
        FRONT_RIGHT -> frontRight
        REAR_LEFT -> rearLeft
        REAR_RIGHT -> rearRight
    }
}