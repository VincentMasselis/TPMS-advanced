package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonGraph
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.CurrentVehicleDropdownViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleGraphCacheUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory

public interface FeatureMainGraph {

    public fun currentVehicleUseCase(): CurrentVehicleUseCase
    public fun noveltyUseCase(): NoveltyUseCase
    public fun vehicleListUseCase(): VehicleListUseCase

    public companion object : InternalGraph by InternalGraph.Factory.build(
        CoreCommonGraph,
        DataUnitGraph,
        DataVehicleComponent,
        DataAppComponent
    )
}

@Suppress("PropertyName", "unused")
@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
internal interface InternalGraph : FeatureMainGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes coreCommonGraph: CoreCommonGraph,
            @Includes dataUnitGraph: DataUnitGraph,
            @Includes dataVehicleComponent: DataVehicleComponent,
            @Includes dataAppComponent: DataAppComponent
        ): InternalGraph

        companion object : Factory by createGraphFactory()
    }

    fun PreconditionsViewModel(): PreconditionsViewModel
    val CurrentVehicleDropdownViewModel: CurrentVehicleDropdownViewModelImpl.Factory
    val vehicleGraphCacheUseCase: VehicleGraphCacheUseCase

    @Provides
    @SingleIn(AppScope::class)
    private fun currentVehicleUseCase(
        database: VehicleDatabase
    ): CurrentVehicleUseCase = CurrentVehicleUseCase(database)

    @Provides
    private fun preconditionsViewModel(
        bluetoothLeScanner: BluetoothLeScanner
    ): PreconditionsViewModel = PreconditionsViewModel(bluetoothLeScanner)

    @Provides
    @SingleIn(AppScope::class)
    private fun vehicleComponentCacheUseCase(
        vehicleDatabase: VehicleDatabase,
    ): VehicleGraphCacheUseCase {
        val factory = createGraphFactory<InternalVehicleGraph.Factory>()
        return VehicleGraphCacheUseCase(vehicleDatabase) { vehicle ->
            factory.build(this, DataVehicleComponent, DataUnitGraph, vehicle)
        }
    }

    @Provides
    @SingleIn(AppScope::class)
    private fun noveltyUseCase(appPreferences: AppPreferences): NoveltyUseCase =
        NoveltyUseCase(appPreferences)

    @Provides
    @SingleIn(AppScope::class)
    private fun vehicleCountStateFlowUseCase(
        database: VehicleDatabase
    ): VehicleCountStateFlowUseCase = VehicleCountStateFlowUseCase(database)

    @Provides
    private fun vehicleListUseCase(
        database: VehicleDatabase
    ): VehicleListUseCase = VehicleListUseCase(database)
}
