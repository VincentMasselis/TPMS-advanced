@file:Suppress("PropertyName", "VariableNaming")

package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.database.ioc.CoreDatabaseComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory


public interface DataVehicleComponent : DebugComponent {

    public val bluetoothLeScanner: BluetoothLeScanner
    public val vehicleDatabase: VehicleDatabase
    public val sensorDatabase: SensorDatabase
    public val tyreDatabase: TyreDatabase

    public companion object : DataVehicleComponent by InternalComponent
}

@Suppress("unused")
@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class, BluetoothLeScannerBinding::class]
)
internal interface InternalComponent : DataVehicleComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes coreCommonComponent: CoreCommonComponent,
            @Includes coreDatabaseComponent: CoreDatabaseComponent,
        ): InternalComponent
    }

    companion object : InternalComponent by createGraphFactory<Factory>().build(
        CoreCommonComponent,
        CoreDatabaseComponent
    )
}
