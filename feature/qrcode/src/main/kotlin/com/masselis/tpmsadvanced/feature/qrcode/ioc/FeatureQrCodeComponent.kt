package com.masselis.tpmsadvanced.feature.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel
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

    companion object : FeatureQrCodeComponent by DaggerFeatureQrCodeComponent.builder()
        .coreCommonComponent(CoreCommonComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .featureCoreComponent(FeatureCoreComponent)
        .build()
}
