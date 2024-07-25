package com.masselis.tpmsadvanced.core.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import org.koin.core.annotation.Single

@SuppressLint("StaticFieldLeak")
private lateinit var privateContext: Context
public val appContext: Context get() = privateContext

@Single
public fun appContext(): Context = appContext

@Suppress("unused")
public class AppContextInitializer : Initializer<Context> {
    override fun create(context: Context): Context {
        privateContext = context
        return context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
