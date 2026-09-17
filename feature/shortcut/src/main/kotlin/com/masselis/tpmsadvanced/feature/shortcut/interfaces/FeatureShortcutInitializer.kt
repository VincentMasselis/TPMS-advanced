package com.masselis.tpmsadvanced.feature.shortcut.interfaces

import android.content.Context
import com.masselis.tpmsadvanced.core.common.AppGraphReadyInitializer
import com.masselis.tpmsadvanced.feature.shortcut.ioc.Bindings.Companion.ShortcutUseCase

public class FeatureShortcutInitializer : AppGraphReadyInitializer<Unit> {
    override fun create(context: Context) {
        // Initializes shortcutUseCase
        ShortcutUseCase()
    }
}
