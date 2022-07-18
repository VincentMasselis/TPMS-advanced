package com.masselis.tpmsadvanced.unit.ioc

import dagger.Component

@SingleInstance
@Component
public abstract class UnitComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(): UnitComponent
    }
}