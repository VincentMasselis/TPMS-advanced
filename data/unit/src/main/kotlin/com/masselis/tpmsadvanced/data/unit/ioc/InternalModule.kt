package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import org.koin.dsl.module

internal val InternalModule = module {
    single { UnitPreferences(get()) }
}