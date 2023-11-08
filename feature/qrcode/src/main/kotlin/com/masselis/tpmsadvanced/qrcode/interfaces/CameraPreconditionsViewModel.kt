package com.masselis.tpmsadvanced.qrcode.interfaces

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.qrcode.usecase.QrCodeAnalyserUseCase
import javax.inject.Inject

internal class CameraPreconditionsViewModel @Inject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
) : ViewModel() {
    fun requiredPermissions() = qrCodeAnalyserUseCase.requiredPermission()
}
