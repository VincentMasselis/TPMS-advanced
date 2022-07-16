package com.masselis.tpmsadvanced.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

@SuppressLint("StaticFieldLeak")
private lateinit var privateContext: Context
val appContext get() = privateContext

class CommonInitializer : Initializer<Context> {
    override fun create(context: Context): Context {
        privateContext = context
        return context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}