package com.masselis.tpmsadvanced.feature.androidauto.di

import androidx.lifecycle.LifecycleOwner
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.viewmodel.TabScreenViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("PropertyName", "FunctionNaming", "VariableNaming")
@DependencyGraph(AppScope::class)
internal interface InternalComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes featureMainComponent: FeatureMainComponent): InternalComponent
    }

    val TabScreenViewModel: TabScreenViewModel.Factory

    context(owner: LifecycleOwner)
    fun TabScreenViewModel() = TabScreenViewModel(owner)

    companion object : InternalComponent by createGraphFactory<Factory>()
        .build(FeatureMainComponent)
}
