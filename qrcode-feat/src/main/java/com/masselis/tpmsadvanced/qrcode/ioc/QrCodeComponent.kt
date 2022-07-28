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
public abstract class QrCodeComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(coreComponent: CoreComponent): QrCodeComponent
    }

    internal abstract val cameraPreconditionsViewModel: CameraPreconditionsViewModel.Factory
    internal abstract val qrCodeViewModel: QRCodeViewModel.Factory
}