package com.masselis.tpmsadvanced.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel
import dagger.Component

@Suppress("PropertyName")
@FeatureQrCodeComponent.Scope
@Component(
    dependencies = [
        CoreCommonComponent::class,
        DataVehicleComponent::class,
        FeatureCoreComponent::class
    ]
)
internal interface FeatureQrCodeComponent {
    @javax.inject.Scope
    annotation class Scope

    @Suppress("VariableNaming")
    val QrCodeViewModel: QRCodeViewModel.Factory

    companion object : FeatureQrCodeComponent by DaggerFeatureQrCodeComponent
        .builder()
        .coreCommonComponent(CoreCommonComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .featureCoreComponent(FeatureCoreComponent)
        .build()
}
