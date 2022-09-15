package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.TyreLocationQualifier
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_RIGHT
import javax.inject.Inject

public class FindTyreComponentUseCase @Inject internal constructor(
    @TyreLocationQualifier(FRONT_LEFT) private val frontLeft: TyreComponent,
    @TyreLocationQualifier(FRONT_RIGHT) private val frontRight: TyreComponent,
    @TyreLocationQualifier(REAR_LEFT) private val rearLeft: TyreComponent,
    @TyreLocationQualifier(REAR_RIGHT) private val rearRight: TyreComponent
) {
    internal fun find(location: TyreLocation) = when (location) {
        FRONT_LEFT -> frontLeft
        FRONT_RIGHT -> frontRight
        REAR_LEFT -> rearLeft
        REAR_RIGHT -> rearRight
    }
}
