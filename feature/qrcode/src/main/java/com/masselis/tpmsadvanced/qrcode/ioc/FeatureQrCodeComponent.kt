package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.favourite.ioc.DataFavouriteComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component

@SingleInstance
@Component(
    dependencies = [
        DataFavouriteComponent::class,
        FeatureCoreComponent::class
    ]
)
public abstract class FeatureQrCodeComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(
            dataFavouriteComponent: DataFavouriteComponent,
            featureCoreComponent: FeatureCoreComponent
        ): FeatureQrCodeComponent
    }

    internal abstract val cameraPreconditionsViewModel: CameraPreconditionsViewModel.Factory
    internal abstract val qrCodeViewModel: QRCodeViewModel.Factory
}
