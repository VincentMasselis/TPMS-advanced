package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.ioc.MainComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component

@SingleInstance
@Component(
    dependencies = [
        MainComponent::class
    ]
)
interface QrCodeComponent {
    @Component.Factory
    interface Factory {
        fun build(mainComponent: MainComponent): QrCodeComponent
    }

    val cameraPreconditionsViewModel: CameraPreconditionsViewModel.Factory
    val qrCodeViewModel: QRCodeViewModel.Factory
}