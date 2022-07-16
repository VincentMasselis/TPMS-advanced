package com.masselis.tpmsadvanced.interfaces

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.ioc.DaggerMainComponent
import com.masselis.tpmsadvanced.ioc.MainComponent

private lateinit var privateMainComponent: MainComponent
val mainComponent get() = privateMainComponent

class StartInitializer : Initializer<MainComponent> {
    override fun create(context: Context): MainComponent = DaggerMainComponent
        .factory()
        .build(context)
        .also { privateMainComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}