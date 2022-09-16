package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.Component

@SingleInstance
@Component
public abstract class DataUnitComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(): DataUnitComponent
    }

    public abstract val unitPreferences: UnitPreferences
}
