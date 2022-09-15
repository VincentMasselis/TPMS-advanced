package com.masselis.tpmsadvanced.qrcode.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.feature.interfaces.FeatureCoreInitializer
import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.data.favourite.ioc.DataFavouriteInitializer
import com.masselis.tpmsadvanced.data.favourite.ioc.dataFavouriteComponent
import com.masselis.tpmsadvanced.qrcode.ioc.DaggerFeatureQrCodeComponent
import com.masselis.tpmsadvanced.qrcode.ioc.FeatureQrCodeComponent

private lateinit var privateComponent: FeatureQrCodeComponent
public val qrCodeComponent: FeatureQrCodeComponent get() = privateComponent

public class QrCodeInitializer : Initializer<FeatureQrCodeComponent> {
    override fun create(context: Context): FeatureQrCodeComponent = DaggerFeatureQrCodeComponent
        .factory()
        .build(dataFavouriteComponent, featureCoreComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        DataFavouriteInitializer::class.java,
        FeatureCoreInitializer::class.java
    )

}
