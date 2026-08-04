package com.masselis.tpmsadvanced.core.common

import androidx.startup.Initializer

public interface AppGraphReadyInitializer<T> : Initializer<T> {
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(appGraphInitializerClass)
}

@Suppress("UNCHECKED_CAST")
public val appGraphInitializerClass: Class<out Initializer<*>> =
    Class.forName($$"com.masselis.tpmsadvanced.ioc.AppGraph$Initializer") as Class<out Initializer<*>>
