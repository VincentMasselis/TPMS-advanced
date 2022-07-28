package com.masselis.tpmsadvanced.core.usecase

import com.masselis.tpmsadvanced.core.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordUseCase {
    fun listen(): Flow<Record>
}