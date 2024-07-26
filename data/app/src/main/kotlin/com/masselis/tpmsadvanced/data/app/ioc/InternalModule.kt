package com.masselis.tpmsadvanced.data.app.ioc

import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import org.koin.dsl.module

internal val InternalModule = module {
    single { AppPreferences(get()) }
}