package com.masselis.tpmsadvanced.data.car.ioc

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.TyreDatabase
import dagger.Component

@SingleInstance
@Component(
    modules = [Module::class]
)
public interface DataCarComponent {

    public val debugComponentFactory: DebugComponent.Factory

    public val car: CarDatabase
    public val sensor: SensorDatabase
    public val tyreDatabase: TyreDatabase

    public companion object : DataCarComponent by DaggerDataCarComponent.create()
}
