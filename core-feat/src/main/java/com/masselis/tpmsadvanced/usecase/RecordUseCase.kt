package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordUseCase {
    fun listen(): Flow<Record>
}