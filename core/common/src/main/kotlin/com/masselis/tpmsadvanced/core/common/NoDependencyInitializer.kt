package com.masselis.tpmsadvanced.core.common

import androidx.startup.Initializer

public interface NoDependencyInitializer<T> : Initializer<T> {
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
