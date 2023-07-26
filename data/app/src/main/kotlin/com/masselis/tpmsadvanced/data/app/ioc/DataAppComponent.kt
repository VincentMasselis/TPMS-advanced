package com.masselis.tpmsadvanced.data.app.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import dagger.Component

@DataAppComponent.Scope
@Component(
    dependencies = [CoreCommonComponent::class]
)
public interface DataAppComponent {
    @Component.Factory
    public interface Factory {
        public fun build(coreCommonComponent: CoreCommonComponent = CoreCommonComponent): DataAppComponent
    }

    public val appPreferences: AppPreferences

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataAppComponent by DaggerDataAppComponent
        .factory()
        .build()
}
