package com.masselis.tpmsadvanced.qrcode.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer
import com.masselis.tpmsadvanced.core.common.coreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.FeatureCoreInitializer
import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataCarInitializer
import com.masselis.tpmsadvanced.data.car.ioc.dataCarComponent
import com.masselis.tpmsadvanced.qrcode.ioc.DaggerFeatureQrCodeComponent
import com.masselis.tpmsadvanced.qrcode.ioc.FeatureQrCodeComponent

private lateinit var privateComponent: FeatureQrCodeComponent
public val qrCodeComponent: FeatureQrCodeComponent get() = privateComponent

public class QrCodeInitializer : Initializer<FeatureQrCodeComponent> {
    override fun create(context: Context): FeatureQrCodeComponent = DaggerFeatureQrCodeComponent
        .factory()
        .build(coreCommonComponent, dataCarComponent, featureCoreComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java,
        DataCarInitializer::class.java,
        FeatureCoreInitializer::class.java
    )

}
