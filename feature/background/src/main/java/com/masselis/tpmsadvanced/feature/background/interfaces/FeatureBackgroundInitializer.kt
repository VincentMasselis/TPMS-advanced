package com.masselis.tpmsadvanced.feature.background.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.AppContextInitializer
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent

@Suppress("unused")
internal class FeatureBackgroundInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        // Calling FeatureBackgroundComponent initializes it
        FeatureBackgroundComponent
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AppContextInitializer::class.java
    )

}
