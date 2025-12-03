package com.masselis.tpmsadvanced.feature.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("PropertyName", "VariableNaming", "unused")
@DependencyGraph(
    bindingContainers = [Bindings::class]
)
internal interface FeatureQrCodeComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes coreCommonComponent: CoreCommonComponent,
            @Includes dataVehicleComponent: DataVehicleComponent,
            @Includes featureMainComponent: FeatureMainComponent,
        ): FeatureQrCodeComponent
    }

    val QrCodeViewModel: QRCodeViewModel.Factory

    companion object : FeatureQrCodeComponent by createGraphFactory<Factory>().build(
        CoreCommonComponent,
        DataVehicleComponent,
        FeatureMainComponent
    )
}
