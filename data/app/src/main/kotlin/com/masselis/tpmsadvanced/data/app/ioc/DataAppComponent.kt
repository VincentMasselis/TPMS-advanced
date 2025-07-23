package com.masselis.tpmsadvanced.data.app.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonGraph
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import dagger.Component

@DataAppComponent.Scope
@Component(
    dependencies = [CoreCommonGraph::class]
)
public interface DataAppComponent {

    public val appPreferences: AppPreferences

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataAppComponent by DaggerDataAppComponent
        .builder()
        .coreCommonGraph(CoreCommonGraph)
        .build()
}
