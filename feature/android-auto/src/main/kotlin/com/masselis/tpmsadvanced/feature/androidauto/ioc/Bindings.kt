package com.masselis.tpmsadvanced.feature.androidauto.ioc

import androidx.lifecycle.LifecycleOwner
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.viewmodel.TabScreenViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject

@Suppress("FunctionNaming", "VariableNaming")
@ContributesTo(AppScope::class)
public interface Bindings {

    public val featureAndroidAutoInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val tabScreenViewModel: TabScreenViewModel.Factory
    )

    public companion object : Bindings by appGraph as Bindings {
        context(owner: LifecycleOwner)
        internal fun TabScreenViewModel() = featureAndroidAutoInternal.tabScreenViewModel(owner)
    }
}
