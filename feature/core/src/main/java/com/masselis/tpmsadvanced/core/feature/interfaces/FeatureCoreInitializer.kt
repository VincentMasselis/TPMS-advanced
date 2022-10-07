package com.masselis.tpmsadvanced.core.feature.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.CoreCommonInitializer
import com.masselis.tpmsadvanced.core.common.coreCommonComponent
import com.masselis.tpmsadvanced.core.feature.ioc.DaggerFeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.unit.ioc.FeatureUnitInitializer
import com.masselis.tpmsadvanced.core.feature.unit.ioc.featureUnitComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataCarInitializer
import com.masselis.tpmsadvanced.data.car.ioc.dataCarComponent
import com.masselis.tpmsadvanced.data.favourite.ioc.DataFavouriteInitializer
import com.masselis.tpmsadvanced.data.favourite.ioc.dataFavouriteComponent
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordInitializer
import com.masselis.tpmsadvanced.data.record.ioc.dataRecordComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitInitializer
import com.masselis.tpmsadvanced.data.unit.ioc.dataUnitComponent

private lateinit var privateComponent: FeatureCoreComponent
public val featureCoreComponent: FeatureCoreComponent get() = privateComponent

public class FeatureCoreInitializer : Initializer<FeatureCoreComponent> {
    override fun create(context: Context): FeatureCoreComponent = DaggerFeatureCoreComponent
        .factory()
        .build(
            coreCommonComponent,
            dataRecordComponent,
            dataUnitComponent,
            dataFavouriteComponent,
            dataCarComponent,
            featureUnitComponent,
        )
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreCommonInitializer::class.java,
        DataRecordInitializer::class.java,
        DataUnitInitializer::class.java,
        DataFavouriteInitializer::class.java,
        DataCarInitializer::class.java,
        FeatureUnitInitializer::class.java,
    )
}
