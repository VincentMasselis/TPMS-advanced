package com.masselis.tpmsadvanced.feature.shortcut.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.core.common.AppContextInitializer
import com.masselis.tpmsadvanced.feature.shortcut.usecase.FeatureShortcutComponent

public class FeatureShortcutInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        // Calling FeatureShortcutComponent initializes it
        FeatureShortcutComponent
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AppContextInitializer::class.java
    )

}
