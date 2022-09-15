package com.masselis.tpmsadvanced.data.favourite.ioc

import android.content.Context
import androidx.startup.Initializer

private lateinit var privateComponent: DataFavouriteComponent
public val dataFavouriteComponent: DataFavouriteComponent get() = privateComponent

public class DataFavouriteInitializer : Initializer<DataFavouriteComponent> {
    override fun create(context: Context): DataFavouriteComponent = DaggerDataFavouriteComponent
        .factory()
        .build()
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
