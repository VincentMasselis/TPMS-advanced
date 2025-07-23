package com.masselis.tpmsadvanced.feature.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonGraph
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainGraph
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel
import dagger.Component

@Suppress("PropertyName")
@FeatureQrCodeComponent.Scope
@Component(
    dependencies = [
        CoreCommonGraph::class,
        DataVehicleComponent::class,
        FeatureMainGraph::class
    ]
)
internal interface FeatureQrCodeComponent {
    @javax.inject.Scope
    annotation class Scope

    @Suppress("VariableNaming")
    val QrCodeViewModel: QRCodeViewModel.Factory

    companion object : FeatureQrCodeComponent by DaggerFeatureQrCodeComponent.builder()
        .coreCommonGraph(CoreCommonGraph)
        .dataVehicleComponent(DataVehicleComponent)
        .featureCoreComponent(FeatureMainGraph)
        .build()
}
