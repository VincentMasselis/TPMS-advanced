package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.ioc.CoreComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component

@SingleInstance
@Component(
    dependencies = [
        CoreComponent::class
    ]
)
interface QrCodeComponent {
    @Component.Factory
    interface Factory {
        fun build(coreComponent: CoreComponent): QrCodeComponent
    }

    val cameraPreconditionsViewModel: CameraPreconditionsViewModel.Factory
    val qrCodeViewModel: QRCodeViewModel.Factory
}