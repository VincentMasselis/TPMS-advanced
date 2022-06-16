package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.model.TyreAtmosphere
import com.masselis.tpmsadvanced.model.TyreLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TyreAtmosphereUseCase @Inject constructor(
    private val location: TyreLocation
) {

    fun listen(): Flow<TyreAtmosphere> = TODO()
}