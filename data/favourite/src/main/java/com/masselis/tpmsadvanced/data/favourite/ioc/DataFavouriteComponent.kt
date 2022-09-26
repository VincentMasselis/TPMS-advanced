package com.masselis.tpmsadvanced.data.favourite.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.favourite.repository.FavouriteRepository
import dagger.Component

@SingleInstance
@Component(
    dependencies = [CoreCommonComponent::class]
)
public abstract class DataFavouriteComponent {
    @Component.Factory
    internal abstract class Factory {
        internal abstract fun build(coreCommonComponent: CoreCommonComponent): DataFavouriteComponent
    }

    public abstract val repository: FavouriteRepository
}
