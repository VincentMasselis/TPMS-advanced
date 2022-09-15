package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.flow.Flow

internal interface TyreUseCase {
    fun listen(): Flow<Tyre>
}
