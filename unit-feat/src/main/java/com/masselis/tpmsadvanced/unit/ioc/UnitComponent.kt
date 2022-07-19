package com.masselis.tpmsadvanced.unit.ioc

import com.masselis.tpmsadvanced.unit.interfaces.UnitsViewModel
import com.masselis.tpmsadvanced.unit.usecase.UnitUseCase
import dagger.Component

@SingleInstance
@Component
public abstract class UnitComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(): UnitComponent
    }

    internal abstract val unitsViewModel: UnitsViewModel

    public abstract val unitUseCase: UnitUseCase
}