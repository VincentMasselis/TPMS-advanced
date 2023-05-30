package com.masselis.tpmsadvanced.qrcode.interfaces

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.qrcode.usecase.QrCodeAnalyserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class CameraPreconditionsViewModel @AssistedInject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): CameraPreconditionsViewModel
    }

    fun requiredPermissions() = qrCodeAnalyserUseCase.requiredPermission()
}
