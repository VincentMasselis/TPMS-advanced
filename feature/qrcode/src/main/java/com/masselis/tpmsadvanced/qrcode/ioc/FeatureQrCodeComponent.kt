package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataCarComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component
import javax.inject.Inject

@SingleInstance
@Component(
    dependencies = [
        CoreCommonComponent::class,
        DataCarComponent::class,
        FeatureCoreComponent::class
    ]
)
public interface FeatureQrCodeComponent {
    @Component.Factory
    public interface Factory {
        public fun build(
            coreCommonComponent: CoreCommonComponent = CoreCommonComponent,
            dataCarComponent: DataCarComponent = DataCarComponent,
            featureCoreComponent: FeatureCoreComponent = FeatureCoreComponent
        ): FeatureQrCodeComponent
    }

    public fun inject(companion: Injectable)

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureQrCodeComponent by DaggerFeatureQrCodeComponent
            .factory()
            .build() {

        @Inject
        internal lateinit var qrCodeViewModel: QRCodeViewModel.Factory

        @Inject
        internal lateinit var cameraPreconditionsViewModel: CameraPreconditionsViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }

    }
}
