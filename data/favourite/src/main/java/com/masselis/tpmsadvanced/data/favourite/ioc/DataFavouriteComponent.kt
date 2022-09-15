package com.masselis.tpmsadvanced.data.favourite.ioc

import com.masselis.tpmsadvanced.data.favourite.repository.FavouriteRepository
import dagger.Component

@SingleInstance
@Component
public abstract class DataFavouriteComponent {
    @Component.Factory
    internal abstract class Factory {
        internal abstract fun build(): DataFavouriteComponent
    }

    public abstract val repository: FavouriteRepository
}
