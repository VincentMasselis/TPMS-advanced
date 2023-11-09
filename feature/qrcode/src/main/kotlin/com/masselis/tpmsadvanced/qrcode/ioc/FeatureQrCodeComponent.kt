package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component
import javax.inject.Inject
import javax.inject.Provider

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
    public abstract class Factory {
        internal abstract fun build(
            coreCommonComponent: CoreCommonComponent,
            dataVehicleComponent: DataVehicleComponent,
            featureCoreComponent: FeatureCoreComponent
        ): FeatureQrCodeComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public fun inject(companion: Injectable)

    public companion object : Injectable()

    @Suppress("PropertyName", "VariableNaming")
    public abstract class Injectable protected constructor() :
        FeatureQrCodeComponent by DaggerFeatureQrCodeComponent
            .factory()
            .build(CoreCommonComponent, DataVehicleComponent, FeatureCoreComponent) {

        @Inject
        internal lateinit var QrCodeViewModel: QRCodeViewModel.Factory

        @Inject
        internal lateinit var CameraPreconditionsViewModel: Provider<CameraPreconditionsViewModel>

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }
}
