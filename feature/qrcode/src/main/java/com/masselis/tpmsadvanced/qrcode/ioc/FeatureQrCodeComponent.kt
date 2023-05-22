package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component
import javax.inject.Inject

@FeatureQrCodeComponent.Scope
@Component(
    dependencies = [
        CoreCommonComponent::class,
        DataVehicleComponent::class,
        FeatureCoreComponent::class
    ]
)
public interface FeatureQrCodeComponent {
    @Component.Factory
    public interface Factory {
        public fun build(
            coreCommonComponent: CoreCommonComponent = CoreCommonComponent,
            dataVehicleComponent: DataVehicleComponent = DataVehicleComponent,
            featureCoreComponent: FeatureCoreComponent = FeatureCoreComponent
        ): FeatureQrCodeComponent
    }

    @javax.inject.Scope
    public annotation class Scope

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
