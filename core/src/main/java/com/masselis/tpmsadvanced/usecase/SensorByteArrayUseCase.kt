package com.masselis.tpmsadvanced.usecase

import kotlinx.coroutines.flow.Flow

interface SensorByteArrayUseCase {
    fun listen(): Flow<ByteArray>
}